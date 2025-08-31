package io.gitlab.arturbosch.detekt.rules.style.movelambdaout

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.unpackFunctionLiteral

// source from https://github.com/JetBrains/intellij-community/blob/88e23175cefa446adb4aa64dda8096112a37e2f8/plugins/kotlin/core/src/org/jetbrains/kotlin/idea/core/psiModificationUtils.kt
@Suppress("ReturnCount")
private fun KtCallExpression.canMoveLambdaOutsideParentheses(): Boolean {
    if (isEligible().not()) return false
    if (getStrictParentOfType<KtDelegatedSuperTypeEntry>() != null) return false
    val lastLambdaExpression = getLastLambdaExpression() ?: return false
    if (lastLambdaExpression.parentLabeledExpression()
            ?.parentLabeledExpression() != null
    ) {
        return false
    }

    val callee = calleeExpression
    if (callee is KtNameReferenceExpression) {
        analyze(callee) {
            val lambdaArgumentCount =
                valueArguments.mapNotNull { it.getArgumentExpression()?.unpackFunctionLiteral() }
                    .count()
            val referenceArgumentCount =
                valueArguments.count { it.getArgumentExpression() is KtCallableReferenceExpression }

            val candidate =
                callee.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
                    ?: return false
            val params = when (candidate) {
                is KaFunctionCall -> {
                    candidate.symbol.valueParameters.map { it.returnType }
                }

                is KaVariableAccessCall -> {
                    (candidate.symbol.returnType as? KaFunctionType)?.parameterTypes.orEmpty()
                }
            }
            val lastParamType = params.lastOrNull() ?: return false
            return lastParamType.isFunctionOrSuspendingFunctionOrGenericType &&
                params.count { it.isFunctionOrSuspendingFunctionOrGenericType } ==
                lambdaArgumentCount + referenceArgumentCount
        }
    }
    return true
}

context(session: KaSession)
private val KaType.isFunctionOrSuspendFunctionType: Boolean
    get() = with(session) {
        this@isFunctionOrSuspendFunctionType.isFunctionType ||
            this@isFunctionOrSuspendFunctionType.isSuspendFunctionType
    }

context(session: KaSession)
private val KaType.isFunctionOrSuspendingFunctionOrGenericType: Boolean
    get() = with(session) {
        this@isFunctionOrSuspendingFunctionOrGenericType.isFunctionOrSuspendFunctionType ||
            this@isFunctionOrSuspendingFunctionOrGenericType is KaTypeParameterType
    }

private fun KtCallExpression.isEligible(): Boolean =
    when {
        valueArguments.lastOrNull()?.isNamed() == true -> false
        valueArguments.count {
            it.getArgumentExpression()?.unpackFunctionLiteral() != null
        } > 1 -> false

        else -> true
    }

internal fun shouldReportUnnecessaryBracesAroundTrailingLambda(
    element: KtCallExpression,
) = element.canMoveLambdaOutsideParentheses()

private fun KtCallExpression.getLastLambdaExpression(): KtLambdaExpression? {
    if (lambdaArguments.isNotEmpty()) return null
    return valueArguments.lastOrNull()?.getArgumentExpression()?.unpackFunctionLiteral()
}

private fun KtExpression.parentLabeledExpression(): KtLabeledExpression? =
    getStrictParentOfType<KtLabeledExpression>()?.takeIf { it.baseExpression == this }
