package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

private fun KtLambdaExpression.firstParameter(bindingContext: BindingContext) =
    bindingContext[BindingContext.FUNCTION, functionLiteral]?.valueParameters?.singleOrNull()

fun KtLambdaExpression.firstParameterOrNull(): KaValueParameterSymbol? = analyze(this) {
    functionLiteral.symbol.valueParameters.singleOrNull()
}

fun KtLambdaExpression.implicitParameter(bindingContext: BindingContext): ValueParameterDescriptor? =
    if (valueParameters.isNotEmpty()) {
        null
    } else {
        firstParameter(bindingContext)
    }

fun KtLambdaExpression.hasImplicitParameterReference(
    implicitParameter: ValueParameterDescriptor,
    bindingContext: BindingContext,
): Boolean =
    anyDescendantOfType<KtNameReferenceExpression> {
        it.isImplicitParameterReference(this, implicitParameter, bindingContext)
    }

private fun KtNameReferenceExpression.isImplicitParameterReference(
    lambda: KtLambdaExpression,
    implicitParameter: ValueParameterDescriptor,
    bindingContext: BindingContext,
): Boolean =
    text == "it" &&
        getStrictParentOfType<KtLambdaExpression>() == lambda &&
        getResolvedCall(bindingContext)?.resultingDescriptor == implicitParameter

fun KtLambdaExpression.hasImplicitParameterReference(): Boolean {
    if (valueParameters.isNotEmpty()) return false
    analyze(this) {
        val implicitParameter = functionLiteral.symbol.valueParameters.singleOrNull() ?: return false
        return anyDescendantOfType<KtNameReferenceExpression> {
            it.text == "it" && it.mainReference.resolveToSymbol() == implicitParameter
        }
    }
}
