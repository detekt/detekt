package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

fun KtCallExpression.isCalling(fqName: FqName, bindingContext: BindingContext): Boolean =
    isCalling(listOf(fqName), bindingContext)

fun KtCallExpression.isCalling(callableId: CallableId): Boolean =
    isCalling(listOf(callableId))

fun KtCallExpression.isCalling(fqNames: List<FqName>, bindingContext: BindingContext): Boolean {
    if (bindingContext == BindingContext.EMPTY) return false
    return getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in fqNames
}

fun KtCallExpression.isCalling(callableIds: List<CallableId>) = analyze(this) {
    resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId in callableIds
}

fun KtCallExpression.isCallingWithNonNullCheckArgument(
    callableId: CallableId,
): Boolean {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return false
    return argument.isNonNullCheck() && isCalling(callableId)
}
