package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtPostfixExpression

/**
 * Reports calls of the map access methods `map[]` or `map.get()` with a not-null assertion operator `!!`.
 * This may result in a NullPointerException.
 * Preferred access methods are `map[]` without `!!`, `map.getValue()`, `map.getOrDefault()` or `map.getOrElse()`.
 *
 * Based on an IntelliJ IDEA inspection MapGetWithNotNullAssertionOperatorInspection.
 *
 * <noncompliant>
 *  val map = emptyMap<String, String>()
 *  map["key"]!!
 *
 *  val map = emptyMap<String, String>()
 *  map.get("key")!!
 * </noncompliant>
 *
 * <compliant>
 * val map = emptyMap<String, String>()
 * map["key"]
 *
 * val map = emptyMap<String, String>()
 * map.getValue("key")
 *
 * val map = emptyMap<String, String>()
 * map.getOrDefault("key", "")
 *
 * val map = emptyMap<String, String>()
 * map.getOrElse("key", { "" })
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class MapGetWithNotNullAssertionOperator(config: Config) :
    Rule(
        config,
        "map.get() with not-null assertion operator (!!) can result in a NullPointerException. " +
            "Consider usage of map.getValue(), map.getOrDefault() or map.getOrElse() instead."
    ),
    RequiresAnalysisApi {

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        if (expression.operationToken == KtTokens.EXCLEXCL && expression.isMapGet()) {
            report(Finding(Entity.from(expression), "map.get() with not-null assertion operator (!!)"))
        }
        super.visitPostfixExpression(expression)
    }

    private fun KtPostfixExpression.isMapGet(): Boolean {
        val postfixExpression = baseExpression ?: return false

        val equalsCallableId = CallableId(StandardClassIds.Map, Name.identifier("get"))

        analyze(postfixExpression) {
            return postfixExpression
                .resolveToCall()
                ?.successfulFunctionCallOrNull()
                ?.symbol
                ?.callableId == equalsCallableId
        }
    }
}
