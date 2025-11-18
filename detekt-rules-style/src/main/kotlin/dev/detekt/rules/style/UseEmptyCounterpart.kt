package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Instantiation of an object's "empty" state should use the object's "empty" initializer for clarity purposes.
 *
 * <noncompliant>
 * arrayOf()
 * listOf() // or listOfNotNull()
 * mapOf()
 * sequenceOf()
 * setOf()
 * </noncompliant>
 *
 * <compliant>
 * emptyArray()
 * emptyList()
 * emptyMap()
 * emptySequence()
 * emptySet()
 * </compliant>
 *
 */
@ActiveByDefault(since = "2.0.0")
class UseEmptyCounterpart(config: Config) :
    Rule(
        config,
        """Instantiation of an object's "empty" state should use the object's "empty" initializer."""
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text !in emptyCounterPartsShortName) return

        val fqName = analyze(expression) {
            expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId?.asSingleFqName()
        } ?: return

        val emptyCounterpart = emptyCounterParts[fqName] ?: return

        if (expression.valueArguments.isEmpty()) {
            val message = "${fqName.shortName()} can be replaced with $emptyCounterpart"
            report(Finding(Entity.from(expression), message))
        }
    }

    companion object {
        private val emptyCounterParts = mapOf(
            FqName("kotlin.arrayOf") to "emptyArray",
            FqName("kotlin.collections.listOf") to "emptyList",
            FqName("kotlin.collections.listOfNotNull") to "emptyList",
            FqName("kotlin.collections.mapOf") to "emptyMap",
            FqName("kotlin.sequences.sequenceOf") to "emptySequence",
            FqName("kotlin.collections.setOf") to "emptySet"
        )

        private val emptyCounterPartsShortName = emptyCounterParts.map { it.key.shortName().asString() }.toSet()
    }
}
