package dev.detekt.rules.coroutines

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

/**
 * Report usage of suspending functions within a `finally` section that are not enclosed in a non-cancellable context.
 * Without a non-cancellable context, these functions will not execute if the parent coroutine is cancelled.
 *
 * <noncompliant>
 * launch {
 *     try {
 *         suspendingWork()
 *     } finally {
 *         suspendingCleanup()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * launch {
 *     try {
 *         suspendingWork()
 *     } finally {
 *         withContext(NonCancellable) { suspendingCleanup() }
 *     }
 * }
 * </compliant>
 *
 */
class SuspendFunInFinallySection(config: Config) :
    Rule(
        config,
        "Suspend functions should not be called from a 'finally' section without using 'NonCancellable' " +
            "context as they won't execute if parent coroutine scope is cancelled."
    ),
    RequiresAnalysisApi {

    override fun visitFinallySection(finallySection: KtFinallySection) {
        finallySection.forEachDescendantOfType<KtCallExpression> { expression ->
            if (shouldReport(expression, finallySection)) {
                report(Finding(Entity.from(expression.calleeExpression as PsiElement), description))
            }
        }
    }

    private fun shouldReport(expression: KtCallExpression, topParent: KtFinallySection) =
        analyze(expression) {
            val isSuspend = expression.resolveToCall()
                ?.successfulFunctionCallOrNull()
                ?.isSuspendCall() == true
            if (!isSuspend) return false

            val parentCalls = expression.parentCallsUpTo(topParent)
            val withContextFun = parentCalls.findFunction("kotlinx.coroutines.withContext") ?: return true
            val firstArgument = withContextFun.valueArguments.first()
            !isNonCancellableArgument(firstArgument)
        }

    private fun KaFunctionCall<*>.isSuspendCall() = (symbol as? KaNamedFunctionSymbol)?.isSuspend == true

    private fun KtCallExpression.parentCallsUpTo(topParent: PsiElement) =
        generateSequence(this as PsiElement) { it.parent }
            .takeWhile { it != topParent }
            .filterIsInstance<KtCallExpression>()

    private fun Sequence<KtCallExpression>.findFunction(fqName: String) =
        firstOrNull {
            analyze(it) {
                it.resolveToCall()
                    ?.successfulFunctionCallOrNull()
                    ?.symbol
                    ?.callableId
                    ?.run { asSingleFqName().asString() } == fqName
            }
        }

    private fun isNonCancellableArgument(arg: KtValueArgument) =
        arg.getArgumentExpression()?.let { expression ->
            analyze(expression) {
                expression.expressionType
                    ?.expandedSymbol
                    ?.classId
                    ?.run { asSingleFqName().asString() } == "kotlinx.coroutines.NonCancellable"
            }
        } == true
}
