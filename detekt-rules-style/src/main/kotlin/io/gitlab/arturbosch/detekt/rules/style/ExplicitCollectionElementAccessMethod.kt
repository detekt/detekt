package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.FqName
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
        analyze(expression) {
            val getter = if (expression.calleeExpression?.text == "get") {
                expression.getFunctionSymbol()
            } else {
                null
            } ?: return false

            if (expression.valueArguments.any { it.isSpread }) return false

            return canReplace(getter) && shouldReplace(getter)
        }
    }

    private fun isIndexSetterRecommended(expression: KtCallExpression): Boolean = analyze(expression) {
        when (expression.calleeExpression?.text) {
            "set" -> {
                val setter = expression.getFunctionSymbol()
                if (setter == null) {
                    false
                } else {
                    canReplace(setter) && shouldReplace(setter)
                }
            }
            // `put` isn't an operator function, but can be replaced with indexer when the caller is Map.
            "put" -> isCallerMap(expression)
            else -> false
        } && unusedReturnValue(expression)
    }

    @Suppress("ModifierListSpacing")
    context(session: KaSession)
    private fun KtCallExpression.getFunctionSymbol(): KaNamedFunctionSymbol? = with(session) {
        resolveToCall()?.singleFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol
    }

    @OptIn(KaExperimentalApi::class)
    private fun canReplace(function: KaNamedFunctionSymbol): Boolean {
        // Can't use index operator when insufficient information is available to infer type variable.
        // For now, this is an incomplete check and doesn't report edge cases (e.g. inference using return type).
        val genericParameterTypeNames = function.valueParameters.map { it.returnType.toString() }.toSet()
        val typeParameterNames = function.typeParameters.map { it.name.asString() }
        if (!genericParameterTypeNames.containsAll(typeParameterNames)) return false

        return function.isOperator
    }

    @Suppress("ReturnCount")
    private fun KaSession.shouldReplace(function: KaNamedFunctionSymbol): Boolean {
        // The intent of kotlin operation functions is to support indexed accessed, so should always be replaced.
        val isJava = function.origin.let { it == KaSymbolOrigin.JAVA_SOURCE || it == KaSymbolOrigin.JAVA_LIBRARY }
        if (!isJava) return true

        // It does not always make sense for all Java get/set functions to be replaced by index accessors.
        // Only recommend known collection types.
        val javaClass = (function.containingDeclaration as? KaClassSymbol)?.classId ?: return false
        return javaClass.asSingleFqName().asString() in setOf(
            "java.util.ArrayList",
            "java.util.HashMap",
            "java.util.LinkedHashMap"
        )
    }

    @Suppress("ReturnCount")
    private fun KaSession.isCallerMap(expression: KtCallExpression): Boolean {
        if (expression.valueArguments.size != 2) return false
        val symbol = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.containingSymbol as? KaClassSymbol
            ?: return false

        val mapName = FqName("kotlin.collections.Map")
        if (symbol.classId?.asSingleFqName() == mapName) return true
        if (symbol.superTypes.any { it.symbol?.classId?.asSingleFqName() == mapName }) return true
        return symbol.superTypes.asSequence().flatMap { it.allSupertypes }.any {
            it.symbol?.classId?.asSingleFqName() == mapName
        }
    }

    private fun unusedReturnValue(expression: KtCallExpression): Boolean =
        expression.parent.parent is KtBlockExpression
}
