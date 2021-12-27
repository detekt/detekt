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
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.ErrorType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * In Kotlin functions `get` or `set` can be replaced with the shorter operator — `[]`,
 * see https://kotlinlang.org/docs/operator-overloading.html#indexed-access-operator.
 * Prefer the usage of the indexed access operator `[]` for map or list element access or insert methods.
 *
 * <noncompliant>
 *  val map = Map<String, String>()
 *  map.put("key", "value")
 *  val value = map.get("key")
 * </noncompliant>
 *
 * <compliant>
 *  val map = Map<String, String>()
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
        if (isIndexableGetter(call) || (isIndexableSetter(call) && unusedReturnValue(call))) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun isIndexableGetter(expression: KtCallExpression): Boolean =
        expression.calleeExpression?.text == "get" && isOperatorFunction(expression)

    private fun isIndexableSetter(expression: KtCallExpression): Boolean =
        when (expression.calleeExpression?.text) {
            "set" -> isOperatorFunction(expression)
            // `put` isn't an operator function, but can be replaced with indexer when the caller is Map.
            "put" -> isCallerMap(expression)
            else -> false
        }

    private fun isOperatorFunction(expression: KtCallExpression): Boolean {
        val function = (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)
        return function?.isOperator == true
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
