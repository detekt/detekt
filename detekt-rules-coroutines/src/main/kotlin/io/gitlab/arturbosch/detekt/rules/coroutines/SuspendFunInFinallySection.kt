package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
@RequiresFullAnalysis
class SuspendFunInFinallySection(config: Config) : Rule(
    config,
    "Suspend functions should not be called from a 'finally' section without using 'NonCancellable' " +
        "context as they won't execute if parent coroutine scope is cancelled."
) {
    override fun visitFinallySection(finallySection: KtFinallySection) {
        finallySection.forEachDescendantOfType<KtCallExpression> { expression ->
            if (shouldReport(expression, finallySection)) {
                report(CodeSmell(Entity.from(expression.calleeExpression as PsiElement), description))
            }
        }
    }

    private fun shouldReport(
        expression: KtCallExpression,
        topParent: KtFinallySection
    ): Boolean {
        if (!expression.isSuspendCall()) {
            return false
        }

        val parentCalls = expression.parentCallsUpTo(topParent)
        val withContextFun = parentCalls.findFunction("kotlinx.coroutines.withContext") ?: return true
        val firstArgument = withContextFun.valueArguments.first()
        return !isNonCancellableArgument(firstArgument, bindingContext)
    }

    private fun KtCallExpression.isSuspendCall() =
        (getResolvedCall(bindingContext)?.resultingDescriptor as FunctionDescriptor).isSuspend

    private fun KtCallExpression.parentCallsUpTo(topParent: PsiElement) =
        generateSequence(this as PsiElement) { it.parent }
            .takeWhile { it != topParent }
            .filter { it is KtCallExpression }
            .map { it as KtCallExpression }

    private fun Sequence<KtCallExpression>.findFunction(fqName: String) = firstOrNull {
        it.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString() == fqName
    }

    private fun isNonCancellableArgument(arg: KtValueArgument, context: BindingContext) =
        arg.getArgumentExpression()
            .getResolvedCall(context)
            ?.resultingDescriptor
            ?.returnType
            .toString() == "NonCancellable"
}
