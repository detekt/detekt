package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

fun KtLambdaExpression.implicitParameter(bindingContext: BindingContext): ValueParameterDescriptor? {
    if (valueParameters.isNotEmpty()) return null
    return bindingContext[BindingContext.FUNCTION, functionLiteral]?.valueParameters?.singleOrNull()
}

fun KtLambdaExpression.hasImplicitParameterReference(
    implicitParameter: ValueParameterDescriptor,
    bindingContext: BindingContext
): Boolean {
    return anyDescendantOfType<KtNameReferenceExpression> {
        it.isImplicitParameterReference(this, implicitParameter, bindingContext)
    }
}

private fun KtNameReferenceExpression.isImplicitParameterReference(
    lambda: KtLambdaExpression,
    implicitParameter: ValueParameterDescriptor,
    bindingContext: BindingContext
): Boolean {
    return text == "it" &&
        getStrictParentOfType<KtLambdaExpression>() == lambda &&
        getResolvedCall(bindingContext)?.resultingDescriptor == implicitParameter
}
