package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.hasAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

/**
 * Detect coroutine launches from `@Test` functions outside a `runTest` block.
 * Launching coroutines in tests without using `runTest` could potentially swallow exceptions,
 * altering test results or causing crashes on other unrelated tests.
 *
 * <noncompliant>
 * @@Test
 * fun `test that launches a coroutine`() {
 *     val scope = CoroutineScope(Dispatchers.Unconfined)
 *     scope.launch {
 *         suspendFunction()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * @@Test
 * fun `test that launches a coroutine`() = runTest {
 *     launch {
 *         suspendFunction()
 *     }
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class CoroutineLaunchedInTestWithoutRunTest(config: Config) : Rule(config) {
    private val exploredNamedFunctions = mutableSetOf<KtNamedFunction>()

    override val issue = Issue(
        id = "CoroutineLaunchedInTestWithoutRunTest",
        description = "Launching coroutines in tests without a `runTest` block could swallow exceptions. " +
            "You should use `runTest` to avoid altering test results."
    )

    override fun visitNamedFunction(initialFunction: KtNamedFunction) {
        if (!initialFunction.hasBody()) return
        if (!initialFunction.hasAnnotation(TEST_ANNOTATION_NAME)) return
        if (initialFunction.runsInRunTestBlock()) return

        checkAndReportIfNecessary(initialFunction)
        initialFunction
            .traverseAndGetAllCalledFunctions()
            .forEach {
                checkAndReportIfNecessary(it)
            }
    }

    private fun checkAndReportIfNecessary(function: KtNamedFunction) {
        function
            .anyDescendantOfType<KtDotQualifiedExpression> {
                it.isLaunchingCoroutine()
            }
            .ifTrue {
                report(CodeSmell(issue, Entity.from(function), MESSAGE))
            }
    }

    private fun KtDotQualifiedExpression.isLaunchingCoroutine() = receiverExpression
        .getType(bindingContext)
        ?.fqNameOrNull() == COROUTINE_SCOPE_FQ &&
        getCalleeExpressionIfAny()?.text == "launch"

    private fun KtNamedFunction.traverseAndGetAllCalledFunctions(): List<KtNamedFunction> {
        collectDescendantsOfType<KtExpression>().mapNotNull {
            it.getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.source
                ?.getPsi() as? KtNamedFunction
        }.forEach {
            if (!exploredNamedFunctions.contains(it)) {
                exploredNamedFunctions.add(it)
                exploredNamedFunctions.addAll(it.traverseAndGetAllCalledFunctions())
            }
        }

        return exploredNamedFunctions.toList()
    }

    private fun KtNamedFunction.runsInRunTestBlock() =
        bodyExpression
            .getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameSafe == RUN_TEST_FQ

    companion object {
        private const val MESSAGE =
            "Launching coroutines in tests without a `runTest` block."

        private const val TEST_ANNOTATION_NAME = "Test"
        private val RUN_TEST_FQ = FqName("kotlinx.coroutines.test.runTest")
        private val COROUTINE_SCOPE_FQ = FqName("kotlinx.coroutines.CoroutineScope")
    }
}
