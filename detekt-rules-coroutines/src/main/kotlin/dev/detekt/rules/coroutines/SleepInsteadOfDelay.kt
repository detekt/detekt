package dev.detekt.rules.coroutines

import com.intellij.psi.PsiElement
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.FqName
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
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkAndReport(expression)
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        checkAndReport(expression)
    }

    private fun checkAndReport(expression: KtExpression) {
        analyze(expression) {
            if (expression.isThreadSleepFunction() && shouldReport(expression)) {
                report(Finding(Entity.from(expression), SUSPEND_FUN_MESSAGE))
            }
        }
    }

    context(session: KaSession)
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
            with(session) {
                val symbol = resolveToCall()?.singleFunctionCallOrNull()?.symbol
                    ?: mainReference?.resolveToSymbol() as? KaCallableSymbol
                symbol?.callableId?.asSingleFqName() == FqName("java.lang.Thread.sleep")
            }
        }
    }

    @Suppress("ReturnCount")
    context(session: KaSession)
    private fun getNearestParentForSuspension(psiElement: PsiElement): PsiElement? {
        fun KtValueArgument.isNearestParentForSuspension(): Boolean {
            val parent = this.getParentOfTypes(true, KtCallExpression::class.java) ?: return false
            with(session) {
                val functionCall = parent.resolveToCall()?.singleFunctionCallOrNull() ?: return false
                val functionSymbol = functionCall.symbol as? KaNamedFunctionSymbol ?: return false
                val parameterSymbol = functionCall.argumentMapping[getArgumentExpression()]?.symbol ?: return false
                return functionSymbol.isInline.not() || parameterSymbol.isNoinline || parameterSymbol.isCrossinline
            }
        }
        return psiElement.getParentOfTypesAndPredicate(
            false,
            KtNamedFunction::class.java,
            KtValueArgument::class.java,
            KtLambdaExpression::class.java,
        ) {
            when (it) {
                is KtValueArgument -> it.isNearestParentForSuspension()
                is KtNamedFunction -> true
                is KtLambdaExpression -> it.getParentOfType<KtProperty>(true, KtValueArgument::class.java) != null
                else -> false
            }
        }
    }

    context(session: KaSession)
    private fun PsiElement.isSuspendAllowed(): Boolean =
        when (this) {
            is KtValueArgument -> this.isSuspendAllowed()
            is KtNamedFunction -> this.isSuspendAllowed()
            is KtLambdaExpression -> this.isSuspendAllowed()
            else -> false
        }

    context(session: KaSession)
    private fun KtValueArgument.isSuspendAllowed(): Boolean {
        val parent = this.getParentOfTypes(true, KtCallExpression::class.java) ?: return false
        val argumentExpression = this.getArgumentExpression() ?: return false
        with(session) {
            val parameter = parent.resolveToCall()?.singleFunctionCallOrNull()?.argumentMapping[argumentExpression]
            return parameter?.returnType?.isSuspendFunctionType == true
        }
    }

    context(session: KaSession)
    private fun KtLambdaExpression.isSuspendAllowed(): Boolean {
        val parent = this.getParentOfTypes(true, KtProperty::class.java) ?: return false
        with(session) {
            return parent.symbol.returnType.isSuspendFunctionType
        }
    }

    context(session: KaSession)
    private fun KtNamedFunction.isSuspendAllowed(): Boolean {
        with(session) {
            return (symbol as? KaNamedFunctionSymbol)?.isSuspend == true
        }
    }

    context(session: KaSession)
    private fun shouldReport(expression: KtExpression): Boolean {
        val nearestParentForSuspension = getNearestParentForSuspension(expression) ?: return false
        return nearestParentForSuspension.isSuspendAllowed()
    }

    companion object {
        private const val SUSPEND_FUN_MESSAGE =
            "This use of Thread.sleep() inside a suspend function should be replaced by delay()."
    }
}
