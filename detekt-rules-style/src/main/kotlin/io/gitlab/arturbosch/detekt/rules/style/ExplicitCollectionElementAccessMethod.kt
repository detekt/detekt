package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.java.isFromJava
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.error.ErrorType
import org.jetbrains.kotlin.types.typeUtil.supertypes

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
    RequiresTypeResolution {
    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        val call = expression.selectorExpression as? KtCallExpression ?: return
        if (isIndexGetterRecommended(call) || isIndexSetterRecommended(call)) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }

    private fun isIndexGetterRecommended(expression: KtCallExpression): Boolean {
        val getter = if (expression.calleeExpression?.text == "get") {
            expression.getFunctionDescriptor()
        } else {
            null
        } ?: return false

        if (expression.valueArguments.any { it.isSpread }) return false

        return canReplace(getter) && shouldReplace(getter)
    }

    private fun isIndexSetterRecommended(expression: KtCallExpression): Boolean =
        when (expression.calleeExpression?.text) {
            "set" -> {
                val setter = expression.getFunctionDescriptor()
                if (setter == null) {
                    false
                } else {
                    canReplace(setter) && shouldReplace(setter)
                }
            }
            // `put` isn't an operator function, but can be replaced with indexer when the caller is Map.
            "put" -> isCallerMap(expression)
            else -> false
        } &&
            unusedReturnValue(expression)

    private fun KtCallExpression.getFunctionDescriptor(): FunctionDescriptor? =
        getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor

    private fun canReplace(function: FunctionDescriptor): Boolean {
        // Can't use index operator when insufficient information is available to infer type variable.
        // For now, this is an incomplete check and doesn't report edge cases (e.g. inference using return type).
        val genericParameterTypeNames = function.valueParameters.map { it.original.type.toString() }.toSet()
        val typeParameterNames = function.typeParameters.map { it.name.asString() }
        if (!genericParameterTypeNames.containsAll(typeParameterNames)) return false

        return function.isOperator
    }

    private fun shouldReplace(function: FunctionDescriptor): Boolean {
        // The intent of kotlin operation functions is to support indexed accessed, so should always be replaced.
        if (!function.isFromJava) return true

        // It does not always make sense for all Java get/set functions to be replaced by index accessors.
        // Only recommend known collection types.
        val javaClass = function.containingDeclaration as? ClassDescriptor ?: return false
        return javaClass.fqNameSafe.asString() in setOf(
            "java.util.ArrayList",
            "java.util.HashMap",
            "java.util.LinkedHashMap"
        )
    }

    private fun isCallerMap(expression: KtCallExpression): Boolean {
        val caller = expression.getQualifiedExpressionForSelector()?.receiverExpression
        val type = caller.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType
        if (type == null || type is ErrorType) return false // There is no caller or it can't be resolved.

        val mapName = "kotlin.collections.Map"
        return type.fqNameOrNull()?.asString() == mapName ||
            type.supertypes().any { it.fqNameOrNull()?.asString() == mapName }
    }

    private fun unusedReturnValue(expression: KtCallExpression): Boolean =
        expression.parent.parent is KtBlockExpression
}
