package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * `suspend` functions should not be called inside `runCatching`'s lambda block, because `runCatching` catches all the
 * `Exception`s. For Coroutines to work in all cases, developers should make sure to propagate `CancellationException`
 * exceptions. This means `CancellationException` should never be:
 * * caught and swallowed (even if logged)
 * * caught and propagated to external systems
 * * caught and shown to the user
 *
 * they must always be rethrown in the same context.
 *
 * Using `runCatching` increases this risk of mis-handling cancellation. If you catch and don't rethrow all the
 * `CancellationException`, your coroutines are not cancelled even if you cancel their `CoroutineScope`.
 *
 * This can very easily lead to:
 * * unexpected crashes
 * * extremely hard to diagnose bugs
 * * memory leaks
 * * performance issues
 * * battery drain
 *
 * See reference, [Kotlin doc](https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative).
 *
 * If your project wants to use `runCatching` and `Result` objects, it is recommended to write a `coRunCatching`
 * utility function which immediately re-throws `CancellationException`; and forbid `runCatching` and `suspend`
 * combinations by activating this rule.
 *
 * <noncompliant>
 * @@Throws(IllegalStateException::class)
 * suspend fun bar(delay: Long) {
 *     check(delay <= 1_000L)
 *     delay(delay)
 * }
 *
 * suspend fun foo() {
 *     runCatching {
 *         bar(1_000L)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * @@Throws(IllegalStateException::class)
 * suspend fun bar(delay: Long) {
 *     check(delay <= 1_000L)
 *     delay(delay)
 * }
 *
 * suspend fun foo() {
 *     try {
 *         bar(1_000L)
 *     } catch (e: IllegalStateException) {
 *         // handle error
 *     }
 * }
 *
 * // Alternate
 * @@Throws(IllegalStateException::class)
 * suspend fun foo() {
 *     bar(1_000L)
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class SuspendFunSwallowedCancellation(config: Config) : Rule(config) {
    override val issue = Issue(
        id = "SuspendFunInsideRunCatching",
        severity = Severity.Minor,
        description = "`runCatching` does not propagate `CancellationException`, don't use it with `suspend` lambda " +
            "blocks.",
        debt = Debt.TEN_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return

        if (resultingDescriptor.fqNameSafe != RUN_CATCHING_FQ) return

        fun shouldTraverseInside(element: PsiElement): Boolean =
            expression == element || shouldTraverseInside(element, bindingContext)

        expression.forEachDescendantOfType<KtCallExpression>(::shouldTraverseInside) { descendant ->
            val callableDescriptor = descendant.getResolvedCall(bindingContext)?.resultingDescriptor
            if (callableDescriptor?.isSuspend == true) {
                report(
                    message = "The suspend function call ${callableDescriptor.fqNameSafe.shortName()} called inside " +
                        "`runCatching`. You should either use specific `try-catch` only catching exception that you " +
                        "are expecting or rethrow the `CancellationException` if already caught.",
                    expression
                )
            }
        }

        expression.forEachDescendantOfType<KtForExpression>(::shouldTraverseInside) { descendant ->
            if (descendant.hasSuspendingIterators()) {
                report(
                    message = "The for-loop expression has suspending operator which is called inside " +
                        "`runCatching`. You should either use specific `try-catch` only catching exception that you " +
                        "are expecting or rethrow the `CancellationException` if already caught.",
                    expression
                )
            }
        }
    }

    @Suppress("ReturnCount")
    private fun shouldTraverseInside(psiElement: PsiElement, bindingContext: BindingContext): Boolean {
        return when (psiElement) {
            is KtCallExpression -> {
                val callableDescriptor =
                    (psiElement.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)
                        ?: return false

                callableDescriptor.fqNameSafe != RUN_CATCHING_FQ && callableDescriptor.isInline
            }
            is KtValueArgument -> {
                val callExpression = psiElement.getParentOfType<KtCallExpression>(true) ?: return false
                val valueParameterDescriptor =
                    callExpression.getResolvedCall(bindingContext)?.getParameterForArgument(psiElement) ?: return false

                valueParameterDescriptor.isCrossinline.not() && valueParameterDescriptor.isNoinline.not()
            }
            else -> true
        }
    }

    private fun KtForExpression.hasSuspendingIterators(): Boolean {
        val iteratorResolvedCall = bindingContext[BindingContext.LOOP_RANGE_ITERATOR_RESOLVED_CALL, this.loopRange]
        val loopRangeHasNextResolvedCall =
            bindingContext[BindingContext.LOOP_RANGE_HAS_NEXT_RESOLVED_CALL, this.loopRange]
        val loopRangeNextResolvedCall = bindingContext[BindingContext.LOOP_RANGE_NEXT_RESOLVED_CALL, this.loopRange]
        return listOf(iteratorResolvedCall, loopRangeHasNextResolvedCall, loopRangeNextResolvedCall).any {
            it?.resultingDescriptor?.isSuspend == true
        }
    }

    private fun report(
        message: String,
        expression: KtCallExpression,
    ) {
        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                message
            )
        )
    }

    companion object {
        private val RUN_CATCHING_FQ = FqName("kotlin.runCatching")
    }
}
