package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

/**
 * Reports string interpolation that is only used to insert a literal dollar sign. Multi-dollar interpolation
 * expresses literal dollar signs directly and avoids workarounds such as `${'$'}` and `${"$"}`.
 *
 * <noncompliant>
 * val greeting = """${'$'}Hello"""
 * val dollar = "$"
 * val amount = """${dollar}amount"""
 * </noncompliant>
 *
 * <compliant>
 * val greeting = $$"""$Hello"""
 * val amount = $$"""$amount"""
 * </compliant>
 */
class UseMultiDollarInterpolation(config: Config) :
    Rule(
        config,
        "String interpolation inserts a literal dollar sign instead of expressing it directly."
    ),
    RequiresAnalysisApi {

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)
        if (expression.usesMultiDollarInterpolation()) return

        analyze(expression) {
            expression.entries
                .filterIsInstance<KtStringTemplateEntryWithExpression>()
                .filter { it.expressions.singleOrNull()?.isStaticallyKnownDollar() == true }
                .forEach {
                    report(
                        Finding(
                            Entity.from(it),
                            "Interpolation `${it.text}` inserts a literal dollar sign. " +
                                "Express it directly with multi-dollar interpolation when needed."
                        )
                    )
                }
        }
    }

    private fun KtStringTemplateExpression.usesMultiDollarInterpolation(): Boolean =
        (interpolationPrefix?.interpolationPrefix?.length ?: 1) > 1

    context(session: KaSession)
    private fun KtExpression.isStaticallyKnownDollar(visitedProperties: Set<KtProperty> = emptySet()): Boolean =
        when (this) {
            is KtConstantExpression -> text == DOLLAR_CHARACTER_LITERAL
            is KtStringTemplateExpression -> evaluatesToDollar()
            is KtNameReferenceExpression -> referencedPropertyEvaluatesToDollar(visitedProperties)
            else -> false
        }

    context(session: KaSession)
    private fun KtNameReferenceExpression.referencedPropertyEvaluatesToDollar(
        visitedProperties: Set<KtProperty>,
    ): Boolean {
        val property = resolveImmutablePropertyWithInitializer() ?: return false
        if (property in visitedProperties) return false

        return property.initializer?.isStaticallyKnownDollar(visitedProperties + property) == true
    }

    context(session: KaSession)
    private fun KtNameReferenceExpression.resolveImmutablePropertyWithInitializer(): KtProperty? {
        val symbol = with(session) { mainReference.resolveToSymbol() } as? KaVariableSymbol ?: return null
        if (!symbol.isVal || symbol.modality != KaSymbolModality.FINAL) return null
        val property = symbol.psi as? KtProperty ?: return null
        if (property.initializer == null || property.getter != null || property.delegate != null) return null
        return property
    }

    private fun KtStringTemplateExpression.evaluatesToDollar(): Boolean =
        when (val entry = entries.singleOrNull()) {
            is KtLiteralStringTemplateEntry -> entry.text == DOLLAR
            is KtEscapeStringTemplateEntry -> entry.unescapedValue == DOLLAR
            else -> false
        }

    private companion object {
        const val DOLLAR = "$"
        const val DOLLAR_CHARACTER_LITERAL = "'$'"
    }
}
