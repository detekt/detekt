package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

fun KtLambdaExpression.firstParameter(bindingContext: BindingContext) =
    bindingContext[BindingContext.FUNCTION, functionLiteral]?.valueParameters?.singleOrNull()

fun KtLambdaExpression.implicitParameter(bindingContext: BindingContext): ValueParameterDescriptor? =
    if (valueParameters.isNotEmpty()) {
        null
    } else {
        firstParameter(bindingContext)
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
