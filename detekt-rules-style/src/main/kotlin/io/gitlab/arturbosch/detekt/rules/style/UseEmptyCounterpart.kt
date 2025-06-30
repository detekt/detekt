package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
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
class UseEmptyCounterpart(config: Config) :
    Rule(
        config,
        """Instantiation of an object's "empty" state should use the object's "empty" initializer."""
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (emptyCounterPartsByShortName[expression.calleeExpression?.text] == null) return

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

        private val emptyCounterPartsByShortName = emptyCounterParts.mapKeys { it.key.shortName().asString() }
    }
}
