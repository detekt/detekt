package io.gitlab.arturbosch.detekt.rules.coroutines

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgument
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.calls.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.calls.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.FqName
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
          analyze(expression) {
                    val call = expression.resolveCall()?.singleFunctionCallOrNull()
                    val functionSymbol = call?.symbol as? KaFunctionSymbol
                    
                    if (functionSymbol?.isSuspend == true) {
                        report(
                            CodeSmell(
                                entity = Entity.from(expression),
                                message = "Suspending function '${functionSymbol.name}' should not be called inside a finally block"
                            )
                        )
                    }
                }
        }
    }

     private fun shouldReport(
        expression: KtCallExpression,
        topParent: KtFinallySection,
    ): Boolean {
        return analyze(expression) {
            if (!expression.isSuspendCall()) {
                return@analyze false
            }

            val parentCalls = expression.parentCallsUpTo(topParent)
            val withContextFun = parentCalls.findFunction("kotlinx.coroutines.withContext")
                ?: return@analyze true
            
            val firstArgument = withContextFun.valueArguments.firstOrNull()
                ?: return@analyze true
            
            !isNonCancellableArgument(firstArgument)
        }
    }

  private fun KtCallExpression.isSuspendCall(): Boolean {
        return analyze(this) {
            val call = this@isSuspendCall.resolveCall()?.singleFunctionCallOrNull()
            val functionSymbol = call?.symbol as? KaFunctionSymbol
            functionSymbol?.isSuspend == true
        }
    }
    private fun KtCallExpression.parentCallsUpTo(topParent: PsiElement) =
        generateSequence(this as PsiElement) { it.parent }
            .takeWhile { it != topParent }
            .filter { it is KtCallExpression }
            .map { it as KtCallExpression }

      private fun Sequence<KtCallExpression>.findFunction(fqName: String): KtCallExpression? {
        return firstOrNull { call ->
            analyze(call) {
                val resolvedCall = call.resolveCall()?.singleFunctionCallOrNull()
                val functionSymbol = resolvedCall?.symbol as? KaFunctionSymbol
                functionSymbol?.callableId?.asSingleFqName()?.asString() == fqName
            }
        }
    }

  private fun isNonCancellableArgument(arg: KtValueArgument): Boolean {
        val argumentExpression = arg.getArgumentExpression() ?: return false
        return analyze(argumentExpression) {
            val call = argumentExpression.resolveCall()?.singleFunctionCallOrNull()
            val symbol = call?.symbol
            symbol?.callableId?.asSingleFqName()?.asString() == "kotlinx.coroutines.NonCancellable"
        }
    }
