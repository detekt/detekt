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
 * Reports string interpolation only used to insert a literal dollar sign. In regular strings, the dollar sign
 * can be escaped or expressed with multi-dollar interpolation. In raw strings, multi-dollar interpolation avoids
 * workarounds such as `${'$'}` and `${"$"}`. The rule also reports immutable property references that are statically
 * known to evaluate to a dollar sign and unnecessary interpolation inside strings that already use a multi-dollar
 * prefix.
 *
 * <noncompliant>
 * val regularGreeting = "${'$'}Hello"
 * val rawGreeting = """${"$"}Hello"""
 * val dollar = "$"
 * val amount = """${dollar}amount"""
 * val price = $$"$${'$'}100"
 * </noncompliant>
 *
 * <compliant>
 * val regularGreeting = "\$Hello"
 * val rawGreeting = $$"""$Hello"""
 * val amount = $$"""$amount"""
 * val price = $$"$100"
 * </compliant>
 */
class AvoidDollarLiteralInterpolation(config: Config) :
    Rule(
        config,
        "String interpolation inserts a literal dollar sign instead of expressing it directly."
    ),
    RequiresAnalysisApi {

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
        super.visitStringTemplateExpression(expression)

        expression.entries
            .filterIsInstance<KtStringTemplateEntryWithExpression>()
            .mapNotNull { entry -> expression.findViolationIn(entry) }
            .forEach { violation -> reportFindingBy(violation) }
    }

    private fun KtStringTemplateExpression.findViolationIn(
        entry: KtStringTemplateEntryWithExpression,
    ): DollarLiteralViolation? {
        if (entry.expressions.singleOrNull()?.isStaticallyKnownDollar() != true) return null

        return when {
            usesMultiDollarInterpolation() -> DollarLiteralViolation.InMultiDollarString(entry)
            isRawString() -> DollarLiteralViolation.InRawString(entry)
            else -> DollarLiteralViolation.InRegularString(entry)
        }
    }

    private fun KtStringTemplateExpression.usesMultiDollarInterpolation(): Boolean =
        (interpolationPrefix?.interpolationPrefix?.length ?: 1) > 1

    private fun KtStringTemplateExpression.isRawString(): Boolean =
        text.startsWith(TRIPLE_QUOTE, interpolationPrefix?.textLength ?: 0)

    private fun KtExpression.isStaticallyKnownDollar(visitedProperties: Set<KtProperty> = emptySet()): Boolean =
        when (this) {
            is KtConstantExpression -> text == DOLLAR_CHARACTER_LITERAL
            is KtStringTemplateExpression -> evaluatesToDollar()
            is KtNameReferenceExpression -> referencedPropertyEvaluatesToDollar(visitedProperties)
            else -> false
        }

    private fun KtNameReferenceExpression.referencedPropertyEvaluatesToDollar(
        visitedProperties: Set<KtProperty>,
    ): Boolean {
        val property = resolveImmutableProperty() ?: return false
        if (property in visitedProperties) return false

        return property.initializer?.isStaticallyKnownDollar(visitedProperties + property) == true
    }

    private fun KtNameReferenceExpression.resolveImmutableProperty(): KtProperty? =
        analyze(this) {
            resolveImmutablePropertyWithInitializerInSession()
        }

    context(session: KaSession)
    private fun KtNameReferenceExpression.resolveImmutablePropertyWithInitializerInSession(): KtProperty? {
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

    private fun reportFindingBy(violation: DollarLiteralViolation) {
        report(
            Finding(
                entity = Entity.from(violation.entry),
                message = "Interpolation `${violation.entry.text}` inserts a literal dollar sign. " +
                    violation.getRecommendationMessage(),
            )
        )
    }

    private companion object {
        const val DOLLAR = "$"
        const val DOLLAR_CHARACTER_LITERAL = "'$'"
        const val TRIPLE_QUOTE = "\"\"\""
    }

    private sealed interface DollarLiteralViolation {
        val entry: KtStringTemplateEntryWithExpression

        fun getRecommendationMessage(): String

        data class InRegularString(override val entry: KtStringTemplateEntryWithExpression) : DollarLiteralViolation {
            override fun getRecommendationMessage(): String =
                "Escape it or use multi-dollar interpolation to express it directly."
        }

        data class InRawString(override val entry: KtStringTemplateEntryWithExpression) : DollarLiteralViolation {
            override fun getRecommendationMessage(): String = "Use multi-dollar interpolation to express it directly."
        }

        data class InMultiDollarString(override val entry: KtStringTemplateEntryWithExpression) :
            DollarLiteralViolation {
            override fun getRecommendationMessage(): String =
                "Express it directly using the existing multi-dollar interpolation prefix."
        }
    }
}
