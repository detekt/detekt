package io.gitlab.arturbosch.detekt.rules.style.movelambdaout

import org.jetbrains.kotlin.builtins.isFunctionOrSuspendFunctionType
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

// source from https://github.com/JetBrains/intellij-community/blob/88e23175cefa446adb4aa64dda8096112a37e2f8/plugins/kotlin/core/src/org/jetbrains/kotlin/idea/core/psiModificationUtils.kt
@Suppress("ReturnCount", "CyclomaticComplexMethod")
private fun KtCallExpression.canMoveLambdaOutsideParentheses(bindingContext: BindingContext): Boolean {
    if (isEligible().not()) return false
    if (getStrictParentOfType<KtDelegatedSuperTypeEntry>() != null) return false
    val lastLambdaExpression = getLastLambdaExpression() ?: kotlin.run {
        return false
    }
    if (lastLambdaExpression.parentLabeledExpression()?.parentLabeledExpression() != null) return false

    val callee = calleeExpression
    if (callee is KtNameReferenceExpression) {
        val lambdaArgumentCount =
            valueArguments.mapNotNull { it.getArgumentExpression()?.unpackFunctionLiteral() }.count()
        val referenceArgumentCount =
            valueArguments.count { it.getArgumentExpression() is KtCallableReferenceExpression }
        val targets = bindingContext[BindingContext.REFERENCE_TARGET, callee]?.let { listOf(it) }
            ?: bindingContext[BindingContext.AMBIGUOUS_REFERENCE_TARGET, callee]
            ?: emptyList()
        val candidates = targets.filterIsInstance<FunctionDescriptor>()
        // if there are functions among candidates but none of them have last function parameter then not show
        // the intention
        @Suppress("ComplexCondition")
        if (
            candidates.isNotEmpty() &&
            candidates.none { candidate ->
                val params = candidate.valueParameters
                val lastParamType = params.lastOrNull()?.type

                (lastParamType?.isFunctionOrSuspendFunctionType == true || lastParamType?.isTypeParameter() == true) &&
                    params.count {
                        it.type.let { type -> type.isFunctionOrSuspendFunctionType || type.isTypeParameter() }
                    } == lambdaArgumentCount + referenceArgumentCount
            }
        ) {
            return false
        }
    }
    return true
}

private fun KtCallExpression.isEligible(): Boolean =
    when {
        valueArguments.lastOrNull()?.isNamed() == true -> false
        valueArguments.count { it.getArgumentExpression()?.unpackFunctionLiteral() != null } > 1 -> false
        else -> true
    }

internal fun shouldReportUnnecessaryBracesAroundTrailingLambda(
    bindingContext: BindingContext,
    element: KtCallExpression,
) =
    element.canMoveLambdaOutsideParentheses(bindingContext)

private fun KtCallExpression.getLastLambdaExpression(): KtLambdaExpression? {
    if (lambdaArguments.isNotEmpty()) return null
    return valueArguments.lastOrNull()?.getArgumentExpression()?.unpackFunctionLiteral()
}

private fun KtExpression.parentLabeledExpression(): KtLabeledExpression? =
    getStrictParentOfType<KtLabeledExpression>()?.takeIf { it.baseExpression == this }
