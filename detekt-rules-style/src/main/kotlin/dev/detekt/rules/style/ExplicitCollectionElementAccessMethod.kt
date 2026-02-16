package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

/**
 * In Kotlin functions `get` or `set` can be replaced with the shorter operator — `[]`,
 * see [Indexed access operator](https://kotlinlang.org/docs/operator-overloading.html#indexed-access-operator).
 * Prefer the usage of the indexed access operator `[]` for map or list element access or insert methods.
 *
 * <noncompliant>
 *  val map = mutableMapOf<String, String>()
 *  map.put("key", "value")
 *  val value = map.get("key")
 * </noncompliant>
 *
 * <compliant>
 *  val map = mutableMapOf<String, String>()
 *  map["key"] = "value"
 *  val value = map["key"]
 * </compliant>
 */
class ExplicitCollectionElementAccessMethod(config: Config) :
    Rule(
        config,
        "Prefer usage of the indexed access operator [] for map element access or insert methods."
    ),
    RequiresAnalysisApi {

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        val call = expression.selectorExpression as? KtCallExpression ?: return
        if (isIndexGetterRecommended(call) || isIndexSetterRecommended(call)) {
            report(Finding(Entity.from(expression), description))
        }
    }

    private fun isIndexGetterRecommended(expression: KtCallExpression): Boolean {
        if (expression.calleeExpression?.text != "get") return false
        analyze(expression) {
            val getter = expression.getFunctionSymbol() ?: return false
            return expression.valueArguments.none { it.isSpread } &&
                expression.valueArguments.isNotEmpty() && // getter must have a minimum of one key
                canReplace(expression, getter) &&
                shouldReplace(getter)
        }
    }

    private fun isIndexSetterRecommended(expression: KtCallExpression): Boolean =
        when (expression.calleeExpression?.text) {
            "set" -> {
                analyze(expression) {
                    val setter = expression.getFunctionSymbol()
                    setter != null &&
                        expression.valueArguments.size >= 2 && // setter must have a minimum of one key and a value
                        canReplace(expression, setter) &&
                        shouldReplace(setter)
                }
            }

            // `put` isn't an operator function, but can be replaced with indexer when the caller is Map.
            "put" -> {
                analyze(expression) {
                    isCallerMap(expression)
                }
            }

            else -> false
        } && unusedReturnValue(expression)

    context(session: KaSession)
    private fun KtCallExpression.getFunctionSymbol(): KaNamedFunctionSymbol? =
        with(session) {
            resolveToCall()?.singleFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol
        }

    private fun canReplace(expression: KtCallExpression, function: KaNamedFunctionSymbol): Boolean {
        if (!function.isOperator) return false
        if (expression.valueArguments.size !in function.expectedArgumentsRange()) return false

        // Can't use index operator when insufficient information is available to infer type variable.
        // For now, this is an incomplete check and doesn't report edge cases (e.g. inference using return type).
        val params = function.valueParameters
        val paramTypeNames = params.map { it.returnType.toString() }.toSet()
        val typeParameterNames = function.typeParameters.map { it.name.asString() }
        return paramTypeNames.containsAll(typeParameterNames)
    }

    // Returns the range of valid call-site argument counts for this function. Used to guard against misresolution
    // when there are compiler errors - the analysis API can sometimes resolve to an unrelated operator function
    // with incompatible parameters
    private fun KaNamedFunctionSymbol.expectedArgumentsRange(): IntRange {
        val required = valueParameters.count { !it.isVararg && !it.hasDefaultValue }
        val max = if (valueParameters.any { it.isVararg }) Int.MAX_VALUE else valueParameters.size
        return required..max
    }

    @Suppress("ReturnCount")
    private fun KaSession.shouldReplace(function: KaNamedFunctionSymbol): Boolean {
        // The intent of kotlin operation functions is to support indexed accessed, so should always be replaced.
        val isJava = function.origin.let { it == KaSymbolOrigin.JAVA_SOURCE || it == KaSymbolOrigin.JAVA_LIBRARY }
        if (!isJava) return true

        // It does not always make sense for all Java get/set functions to be replaced by index accessors.
        // Only recommend known collection types.
        val javaClass = (function.containingDeclaration as? KaClassSymbol)?.classId ?: return false
        return javaClass in setOf(
            ClassId.fromString("java/util/ArrayList"),
            ClassId.fromString("java/util/HashMap"),
            ClassId.fromString("java/util/LinkedHashMap"),
        )
    }

    @Suppress("ReturnCount")
    private fun KaSession.isCallerMap(expression: KtCallExpression): Boolean {
        if (expression.valueArguments.size != 2) return false
        val symbol = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.containingSymbol as? KaClassSymbol
            ?: return false

        val mapClass = ClassId.fromString("kotlin/collections/Map")
        return symbol.classId == mapClass ||
            symbol.superTypes.any { it.symbol?.classId == mapClass } ||
            symbol.superTypes.asSequence().flatMap { it.allSupertypes }.any { it.symbol?.classId == mapClass }
    }

    private fun unusedReturnValue(expression: KtCallExpression): Boolean = expression.parent.parent is KtBlockExpression
}
