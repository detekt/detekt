package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.symbol
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

@OptIn(KaContextParameterApi::class)
context(_: KaSession)
fun KtLambdaExpression.firstParameterOrNull(): KaValueParameterSymbol? =
    functionLiteral.symbol.valueParameters.singleOrNull()

fun KtLambdaExpression.hasImplicitParameter(): Boolean =
    if (valueParameters.isNotEmpty()) {
        false
    } else {
        analyze(this) { firstParameterOrNull() != null }
    }

fun KtLambdaExpression.hasImplicitParameterReference(): Boolean {
    if (valueParameters.isNotEmpty()) return false
    analyze(this) {
        val implicitParameter = functionLiteral.symbol.valueParameters.singleOrNull() ?: return false
        return anyDescendantOfType<KtNameReferenceExpression> {
            @OptIn(KaExperimentalApi::class)
            it.text == "it" && it.resolveSymbol() == implicitParameter
        }
    }
}
