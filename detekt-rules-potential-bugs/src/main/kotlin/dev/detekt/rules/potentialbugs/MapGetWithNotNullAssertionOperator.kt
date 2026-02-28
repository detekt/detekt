package dev.detekt.rules.potentialbugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.successfulVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.name
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression

private val GET_FUNCTION_NAME = Name.identifier("get")
private val KEY_PARAMETER_NAME = Name.identifier("K")
private val VALUE_PARAMETER_NAME = Name.identifier("V")

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

    @OptIn(KaExperimentalApi::class)
    private fun KtPostfixExpression.isMapGet(): Boolean {
        val postfixExpression = baseExpression ?: return false

        val expression = when (postfixExpression) {
            is KtDotQualifiedExpression -> postfixExpression.receiverExpression.takeIf {
                (postfixExpression.selectorExpression as? KtCallExpression)?.calleeExpression?.text == "get"
            }

            is KtArrayAccessExpression -> postfixExpression.arrayExpression

            else -> null
        } ?: return false

        analyze(postfixExpression) {
            val callExpression = expression.resolveToCall()

            val successfulCall = callExpression?.successfulVariableAccessCall()
                ?: callExpression?.successfulFunctionCallOrNull()
                ?: return false

            val callReturnType = successfulCall.symbol.returnType
            if (callReturnType.symbol?.classId != StandardClassIds.Map && !callReturnType.hasMapSuperType()) {
                return false
            }

            val functionSymbol = postfixExpression
                .resolveToCall()
                ?.successfulFunctionCallOrNull()
                ?.symbol
                ?: return false

            return functionSymbol.name == GET_FUNCTION_NAME &&
                functionSymbol.valueParameters.singleOrNull()?.returnTypeName() == KEY_PARAMETER_NAME &&
                functionSymbol.returnTypeName() == VALUE_PARAMETER_NAME
        }
    }

    private fun KaType.hasMapSuperType(): Boolean =
        (this.symbol as? KaNamedClassSymbol)?.superTypes?.any {
            (it.symbol?.classId == StandardClassIds.Map) || it.hasMapSuperType()
        } ?: false

    private fun KaCallableSymbol.returnTypeName(): Name? = (this.returnType as? KaTypeParameterType)?.name
}
