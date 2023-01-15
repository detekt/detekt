package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Report usages of `Thread.sleep` in suspending functions and coroutine blocks. A thread can
 * contain multiple coroutines at one time due to coroutines' lightweight nature, so if one
 * coroutine invokes `Thread.sleep`, it could potentially halt the execution of unrelated coroutines
 * and cause unpredictable behavior.
 *
 * <noncompliant>
 * suspend fun foo() {
 *     Thread.sleep(1_000L)
 * }
 * </noncompliant>
 *
 * <compliant>
 * suspend fun foo() {
 *     delay(1_000L)
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class SleepInsteadOfDelay(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Usage of `Thread.sleep()` in coroutines can potentially halt multiple coroutines at once.",
        Debt.FIVE_MINS
    )

    override fun visitBlockExpression(expression: KtBlockExpression) {
        expression.forEachDescendantOfType<KtCallExpression>(::shouldTraverseInside) {
            it.verifyExpression(SUSPEND_FUN_MESSAGE)
        }

        expression.forEachDescendantOfType<KtCallableReferenceExpression>(::shouldTraverseInside) {
            it.verifyExpression(SUSPEND_FUN_MESSAGE)
        }
    }

    @Suppress("ReturnCount")
    private fun shouldTraverseInside(psiElement: PsiElement): Boolean {
        return when (psiElement) {
            is KtLambdaExpression -> {
                val psiParent = psiElement.parent
                if (psiParent is KtLambdaArgument) {
                    return psiParent.shouldTraverseInside()
                }
                bindingContext[BindingContext.EXPRESSION_TYPE_INFO, psiElement]?.let {
                    it.type?.isSuspendFunctionType == true
                } ?: false
            }
            else -> {
                true
            }
        }
    }

    @Suppress("ReturnCount")
    private fun KtLambdaArgument.shouldTraverseInside(): Boolean {
        val callExpression = this.getParentOfType<KtCallExpression>(true)
        val callDescriptor = callExpression?.getResolvedCall(bindingContext) ?: return false
        val functionDescriptor = callDescriptor.resultingDescriptor as? FunctionDescriptor ?: return false
        val valueParameterDescriptor = callDescriptor.getParameterForArgument(this) ?: return false

        return (
            functionDescriptor.isInline &&
                (valueParameterDescriptor.isNoinline.not() && valueParameterDescriptor.isCrossinline.not())
            ) || valueParameterDescriptor.returnType?.isSuspendFunctionType == true
    }

    private fun KtExpression.verifyExpression(message: String) {
        val fqName = getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString()
        if (fqName == FQ_NAME) {
            report(CodeSmell(issue, Entity.from(this), message))
        }
    }

    private fun KtCallableReferenceExpression.verifyExpression(message: String) {
        if (this.parent is KtValueArgument) {
            // Only checking if this is used as for invocation
            this.callableReference.verifyExpression(message)
        }
    }

    companion object {
        private const val SUSPEND_FUN_MESSAGE =
            "This use of Thread.sleep() inside a suspend function should be replaced by delay()."
        private const val FQ_NAME = "java.lang.Thread.sleep"
    }
}
