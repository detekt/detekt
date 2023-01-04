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
 * Suspend functions should not be called inside `runCatching` as `runCatching` catches
 * all the exception while for Coroutine cooperative cancellation to work, we have to
 * never catch the `CancellationException` exception or rethrowing it again if caught
 *
 * See https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative
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
                        "`CancellationException` if already caught"
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
