package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.java.isFromJava
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.ErrorType
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
@RequiresTypeResolution
class ExplicitCollectionElementAccessMethod(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue =
        Issue(
            "ExplicitCollectionElementAccessMethod",
            Severity.Style,
            "Prefer usage of the indexed access operator [] for map element access or insert methods.",
            Debt.FIVE_MINS
        )

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return
        val call = expression.selectorExpression as? KtCallExpression ?: return
        if (isIndexGetterRecommended(call) || isIndexSetterRecommended(call)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun isIndexGetterRecommended(expression: KtCallExpression): Boolean =
        expression.calleeExpression?.text == "get" && canReplace(expression.getFunctionDescriptor())

    private fun isIndexSetterRecommended(expression: KtCallExpression): Boolean =
        when (expression.calleeExpression?.text) {
            "set" -> {
                val setter = expression.getFunctionDescriptor()
                if (setter == null) false
                else canReplace(setter) && !(setter.isFromJava && setter.valueParameters.size > 2)
            }
            // `put` isn't an operator function, but can be replaced with indexer when the caller is Map.
            "put" -> isCallerMap(expression)
            else -> false
        } && unusedReturnValue(expression)

    private fun KtCallExpression.getFunctionDescriptor(): FunctionDescriptor? =
        getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor

    private fun canReplace(function: FunctionDescriptor?): Boolean {
        if (function == null) return false

        // Can't use index operator when insufficient information is available to infer type variable.
        // For now, this is an incomplete check and doesn't report edge cases (e.g. inference using return type).
        val genericParameterTypeNames = function.valueParameters.map { it.original.type.toString() }.toSet()
        val typeParameterNames = function.typeParameters.map { it.name.asString() }
        if (!genericParameterTypeNames.containsAll(typeParameterNames)) return false

        return function.isOperator
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
