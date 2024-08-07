package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypes
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate
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
@ActiveByDefault(since = "1.21.0")
class SleepInsteadOfDelay(config: Config) :
    Rule(
        config,
        "Usage of `Thread.sleep()` in coroutines can potentially halt multiple coroutines at once."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkAndReport(expression)
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        checkAndReport(expression)
    }

    private fun checkAndReport(expression: KtExpression) {
        if (expression.isThreadSleepFunction() && shouldReport(expression)) {
            report(CodeSmell(Entity.from(expression), SUSPEND_FUN_MESSAGE))
        }
    }

    private fun KtExpression.isThreadSleepFunction(): Boolean {
        fun KtCallableReferenceExpression.isSleepCallableRef(): Boolean =
            if (this.parent is KtValueArgument) {
                // Only checking if this is used as for invocation
                this.callableReference.isThreadSleepFunction()
            } else {
                false
            }
        return if (this is KtCallableReferenceExpression) {
            this.isSleepCallableRef()
        } else {
            getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.fqNameOrNull()
                ?.asString() == FQ_NAME
        }
    }

    @Suppress("ReturnCount")
    private fun getNearestParentForSuspension(psiElement: PsiElement): PsiElement? {
        fun KtValueArgument.isNearestParentForSuspension(): Boolean {
            val parent = this.getParentOfTypes(true, KtCallExpression::class.java) ?: return false
            val callDescriptor = parent.getResolvedCall(bindingContext)
            val valueParameterDescriptor =
                parent.getResolvedCall(bindingContext)
                    ?.getParameterForArgument(this)
                    ?: return false
            val functionDescriptor = callDescriptor?.resultingDescriptor as? FunctionDescriptor ?: return false
            return functionDescriptor.isInline.not() ||
                (valueParameterDescriptor.isNoinline || valueParameterDescriptor.isCrossinline)
        }
        return psiElement.getParentOfTypesAndPredicate(
            false,
            KtNamedFunction::class.java,
            KtValueArgument::class.java,
            KtLambdaExpression::class.java,
        ) {
            when (it) {
                is KtValueArgument -> {
                    it.isNearestParentForSuspension()
                }

                is KtNamedFunction -> {
                    true
                }

                is KtLambdaExpression -> {
                    it.getParentOfType<KtProperty>(true, KtValueArgument::class.java) != null
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun PsiElement.isSuspendAllowed(): Boolean =
        when (this) {
            is KtValueArgument -> {
                this.isSuspendAllowed()
            }

            is KtNamedFunction -> {
                this.isSuspendAllowed()
            }

            is KtLambdaExpression -> {
                this.isSuspendAllowed()
            }

            else -> {
                false
            }
        }

    private fun KtValueArgument.isSuspendAllowed(): Boolean {
        val parent = this.getParentOfTypes(true, KtCallExpression::class.java) ?: return false
        val valueParameterDescriptor =
            parent.getResolvedCall(bindingContext)
                ?.getParameterForArgument(this)
        return valueParameterDescriptor?.returnType?.isSuspendFunctionType == true
    }

    private fun KtLambdaExpression.isSuspendAllowed(): Boolean {
        val parent = this.getParentOfTypes(true, KtProperty::class.java)
            ?: return false
        val properDescriptor = bindingContext[BindingContext.VARIABLE, parent] ?: return false
        return properDescriptor.returnType?.isSuspendFunctionType ?: false
    }

    private fun KtNamedFunction.isSuspendAllowed(): Boolean {
        val functionDescriptor = bindingContext[BindingContext.FUNCTION, this] ?: return false
        return functionDescriptor.isSuspend
    }

    private fun shouldReport(expression: KtExpression): Boolean {
        val nearestParentForSuspension = getNearestParentForSuspension(expression) ?: return false
        return nearestParentForSuspension.isSuspendAllowed()
    }

    companion object {
        private const val SUSPEND_FUN_MESSAGE =
            "This use of Thread.sleep() inside a suspend function should be replaced by delay()."
        private const val FQ_NAME = "java.lang.Thread.sleep"
    }
}
