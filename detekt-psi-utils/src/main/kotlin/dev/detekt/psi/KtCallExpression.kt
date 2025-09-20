package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression

fun KtCallExpression.isCalling(callableId: CallableId): Boolean =
    isCalling(listOf(callableId))

fun KtCallExpression.isCalling(callableIds: List<CallableId>) = analyze(this) {
    resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId in callableIds
}

fun KtCallExpression.isCallingWithNonNullCheckArgument(
    callableId: CallableId,
): Boolean {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return false
    return argument.isNonNullCheck() && isCalling(callableId)
}
