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
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
`suspend` functions should not be called inside `runCatching`'s lambda block, because `runCatching` catches all the `Exception`s. For Coroutines to work in all cases, developers should make sure to propagate `CancellationException` exceptions. This means `CancellationException` should never be:
 * caught and swallowed (even if logged)
 * caught and propagated to external systems
 * caught and shown to the user

they must always be rethrown in the same context.

Using `runCatching` increases this risk of mis-handling cancellation. If you catch and don't rethrow all the `CancellationException`, your coroutines are not cancelled even if you cancel their `CoroutineScope`.

This can very easily lead to:
 * unexpected crashes
 * extremely hard to diagnose bugs
 * memory leaks
 * performance issues
 * battery drain

For reference, see [Kotlin documentation](https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative).

If your project wants to use `runCatching` and `Result` objects, it is recommended to write a `coRunCatching` utility function which immediately re-throws `CancellationException`; and forbid `runCatching` and `suspend` combinations by activating this rule.
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
 * suspend fun foo() {
 *     bar(1_000L)
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class SuspendFunInsideRunCatching(config: Config) : Rule(config) {
    override val issue = Issue(
        id = "SuspendFunInsideRunCatching",
        severity = Severity.Minor,
        description = "The `suspend` functions should be called inside `runCatching` block as it also swallows " +
            "`CancellationException` which is important for cooperative cancellation." +
            "You should either use specific `try-catch` only catching exception that you are expecting" +
            " or rethrow the `CancellationException` if already caught",
        debt = Debt.TEN_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor
        resultingDescriptor ?: return
        if (resultingDescriptor.fqNameSafe != RUN_CATCHING_FQ) return

        expression.forEachDescendantOfType<KtCallExpression> { descendant ->
            if (descendant.getResolvedCall(bindingContext)?.resultingDescriptor?.isSuspend == true && shouldReport(
                    resultingDescriptor,
                    descendant,
                    bindingContext,
                )
            ) {
                val message =
                    "The suspend function call ${descendant.text} is inside `runCatching`. You should either " +
                        "use specific `try-catch` only catching exception that you are expecting or rethrow the " +
                        "`CancellationException` if already caught."
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        message
                    )
                )
            }
        }
    }

    private fun shouldReport(
        runCatchingCallableDescriptor: CallableDescriptor,
        callExpression: KtCallExpression,
        bindingContext: BindingContext,
    ): Boolean {
        val firstNonInlineOrRunCatchingParent =
            callExpression.getParentOfTypesAndPredicate(true, KtCallExpression::class.java) { parentCallExp ->
                val parentCallFunctionDescriptor =
                    parentCallExp.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor
                parentCallFunctionDescriptor ?: return@getParentOfTypesAndPredicate false

                val isParentRunCatching = parentCallFunctionDescriptor.fqNameSafe == RUN_CATCHING_FQ
                val isInline = parentCallFunctionDescriptor.isInline
                val noInlineAndCrossInlineValueParametersIndex =
                    parentCallFunctionDescriptor.valueParameters.filter { valueParameterDescriptor ->
                        valueParameterDescriptor.isCrossinline || valueParameterDescriptor.isNoinline
                    }.map {
                        it.index
                    }
                val callExpressionIndexInParentCall = parentCallExp.valueArguments.indexOfFirst { valueArgument ->
                    valueArgument?.findDescendantOfType<KtCallExpression> {
                        it == callExpression
                    } != null
                }
                isParentRunCatching ||
                    isInline.not() ||
                    noInlineAndCrossInlineValueParametersIndex.contains(callExpressionIndexInParentCall)
            }
        return firstNonInlineOrRunCatchingParent.getResolvedCall(bindingContext)?.resultingDescriptor ==
            runCatchingCallableDescriptor
    }

    companion object {
        private val RUN_CATCHING_FQ = FqName("kotlin.runCatching")
    }
}
