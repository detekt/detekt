package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtCallExpression.isCalling(fqName: FqName, bindingContext: BindingContext): Boolean {
    return bindingContext != BindingContext.EMPTY &&
            calleeExpression?.text == fqName.shortName().asString() &&
            getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe == fqName
}

fun KtCallExpression.isCallingWithNonNullCheckArgument(
    fqName: FqName,
    bindingContext: BindingContext
): Boolean {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return false
    return argument.isNonNullCheck() && isCalling(fqName, bindingContext)
}
