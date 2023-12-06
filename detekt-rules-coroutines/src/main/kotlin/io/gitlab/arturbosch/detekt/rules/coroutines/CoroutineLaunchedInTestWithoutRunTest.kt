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
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

/**
 * Detect coroutine launches from JUnit `@Test` functions outside a runTest block.
 * Launching coroutines in tests without using runTest could potentially swallow exceptions,
 * altering test results or causing crashes or other unrelated tests.
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
 *     val scope = CoroutineScope(Dispatchers.Unconfined)
 *     scope.launch {
 *         suspendFunction()
 *     }
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class CoroutineLaunchedInTestWithoutRunTest(config: Config) : Rule(config) {
    override val issue = Issue(
        id = "CoroutineLaunchedInTestWithoutRunTest",
        description = "Launching coroutines in tests without a `runTest` block could swallow exceptions" +
            "You should use `runTest`to avoid altering test results."
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        checkAndReport(function)
    }

    private fun checkAndReport(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) return
        if (!function.hasBody()) return
        if (!function.hasAnnotation(TEST_ANNOTATION_NAME)) return

        val resultingDescriptor = function.bodyExpression
            .getResolvedCall(bindingContext)
            ?.resultingDescriptor

        if (resultingDescriptor?.fqNameSafe == RUN_TEST_FQ) return

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

    companion object {
        private const val MESSAGE =
            "Launching coroutines in tests without a `runTest` block."

        private const val TEST_ANNOTATION_NAME = "Test"
        private val RUN_TEST_FQ = FqName("kotlinx.coroutines.test.runTest")
        private val COROUTINE_SCOPE_FQ = FqName("kotlinx.coroutines.CoroutineScope")
    }
}
