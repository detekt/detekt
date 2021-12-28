package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtCallExpression.isCalling(fqName: FqName, bindingContext: BindingContext): Boolean {
    return bindingContext != BindingContext.EMPTY &&
        calleeExpression?.text == fqName.shortName().asString() &&
        getResolvedCall(bindingContext)?.isCalling(fqName) == true
}

@Suppress("ReturnCount")
fun KtCallExpression.isCalling(fqNames: List<FqName>, bindingContext: BindingContext): Boolean {
    if (bindingContext == BindingContext.EMPTY) return false
    val calleeText = calleeExpression?.text ?: return false
    val targetFqNames = fqNames.filter { it.shortName().asString() == calleeText }
    if (targetFqNames.isEmpty()) return false
    val resolvedCall = getResolvedCall(bindingContext) ?: return false
    return targetFqNames.any { resolvedCall.isCalling(it) }
}

fun ResolvedCall<out CallableDescriptor>.isCalling(fqName: FqName): Boolean = resultingDescriptor.fqNameSafe == fqName

fun KtCallExpression.isCallingWithNonNullCheckArgument(
    fqName: FqName,
    bindingContext: BindingContext
): Boolean {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression() as? KtBinaryExpression ?: return false
    return argument.isNonNullCheck() && isCalling(fqName, bindingContext)
}
