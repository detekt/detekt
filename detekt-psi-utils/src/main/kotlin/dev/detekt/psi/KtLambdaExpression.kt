package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

fun KtLambdaExpression.firstParameterOrNull(): KaValueParameterSymbol? =
    analyze(this) {
        functionLiteral.symbol.valueParameters.singleOrNull()
    }

fun KtLambdaExpression.implicitParameterOrNull(): KaValueParameterSymbol? =
    if (valueParameters.isNotEmpty()) {
        null
    } else {
        firstParameterOrNull()
    }

fun KtLambdaExpression.hasImplicitParameterReference(): Boolean {
    if (valueParameters.isNotEmpty()) return false
    analyze(this) {
        val implicitParameter = functionLiteral.symbol.valueParameters.singleOrNull() ?: return false
        return anyDescendantOfType<KtNameReferenceExpression> {
            it.text == "it" && it.mainReference.resolveToSymbol() == implicitParameter
        }
    }
}
