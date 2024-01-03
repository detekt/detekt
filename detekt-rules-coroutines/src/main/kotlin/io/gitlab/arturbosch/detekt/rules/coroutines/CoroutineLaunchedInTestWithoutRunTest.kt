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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi

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
    private val funTraverseHelper = FunTraverseHelper()

    override val issue = Issue(
        id = "CoroutineLaunchedInTestWithoutRunTest",
        description = "Launching coroutines in tests without a `runTest` block could swallow exceptions. " +
            "You should use `runTest` to avoid altering test results."
    )

    override fun visitNamedFunction(initialFunction: KtNamedFunction) {
        if (!initialFunction.hasBody()) return
        if (!initialFunction.hasAnnotation(TEST_ANNOTATION_NAME)) return
        if (initialFunction.runsInRunTestBlock()) return

        checkAndReport(initialFunction)

        funTraverseHelper
            .getAllUnexploredCalledFunctions(initialFunction, bindingContext)
            .forEach {
                checkAndReport(it)
            }
    }

    private fun checkAndReport(function: KtNamedFunction) {
        if (function.isLaunchingCoroutine()) {
            report(CodeSmell(issue, Entity.from(function), MESSAGE))
            return
        }
    }

    private fun KtNamedFunction.isLaunchingCoroutine() = anyDescendantOfType<KtDotQualifiedExpression> {
        it.receiverExpression
            .getType(bindingContext)
            ?.fqNameOrNull() == COROUTINE_SCOPE_FQ &&
            it.getCalleeExpressionIfAny()?.text == "launch"
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

class FunTraverseHelper {
    private val exploredFunctionsCache = mutableSetOf<KtNamedFunction>()

    fun getAllUnexploredCalledFunctions(
        initialFunction: KtNamedFunction,
        bindingContext: BindingContext
    ): List<KtNamedFunction> {
        val traversedFunctions = mutableSetOf<KtNamedFunction>()

        fun getChildFunctionsOf(function: KtNamedFunction): Set<KtNamedFunction> {
            function.collectDescendantsOfType<KtExpression>().mapNotNull {
                it.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                    ?.source
                    ?.getPsi() as? KtNamedFunction
            }.forEach {
                if (!exploredFunctionsCache.contains(it)) {
                    traversedFunctions.add(it)
                    exploredFunctionsCache.add(it)

                    getChildFunctionsOf(it).forEach { childFunction ->
                        traversedFunctions.add(childFunction)
                        exploredFunctionsCache.add(childFunction)
                    }
                }
            }

            return traversedFunctions
        }

        getChildFunctionsOf(initialFunction)
        return traversedFunctions.toList()
    }
}
