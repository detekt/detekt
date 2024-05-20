package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

fun KtCallExpression.isCalling(fqName: FqName, bindingContext: BindingContext): Boolean {
    return bindingContext != BindingContext.EMPTY &&
        getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() == fqName
}

fun KtCallExpression.isCalling(fqNames: List<FqName>, bindingContext: BindingContext): Boolean {
    if (bindingContext == BindingContext.EMPTY) return false
    return getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in fqNames
}

fun KtCallExpression.isCallingWithNonNullCheckArgument(
    fqName: FqName,
    bindingContext: BindingContext
): Boolean {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return false
    return argument.isNonNullCheck() && isCalling(fqName, bindingContext)
}
