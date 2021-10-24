package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
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
class ExplicitCollectionElementAccessMethod(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue =
        Issue(
            "ExplicitCollectionElementAccessMethod",
            Severity.Style,
            "Prefer usage of indexed access operator [] for map element access or insert methods",
            Debt.FIVE_MINS
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        val isSafeCall = expression.parent is KtSafeQualifiedExpression

        if (!isSafeCall && (isIndexableGetter(expression) || isIndexableSetter(expression))) {
            report(CodeSmell(issue, Entity.from(expression), "Prefer usage of indexed access operator []."))
        }
        super.visitCallExpression(expression)
    }

    private fun isIndexableGetter(expression: KtCallExpression): Boolean =
        expression.calleeExpression?.text == "get" && isOperatorFunction(expression)

    private fun isIndexableSetter(expression: KtCallExpression): Boolean =
        when (expression.calleeExpression?.text) {
            "set" -> isOperatorFunction(expression)
            "put" -> {
                // Verify whether caller is Map; `put` isn't an operator function, but can be replaced with indexer.
                val caller = (expression.parent as? KtDotQualifiedExpression)?.firstChild as? KtElement
                val type = caller.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType
                val mapName = "kotlin.collections.Map"
                type?.fqNameOrNull()?.asString() == mapName ||
                    type?.supertypes()?.any { it.fqNameOrNull()?.asString() == mapName } ?: false
            }
            else -> false
        }

    private fun isOperatorFunction(expression: KtCallExpression): Boolean
    {
        val function = (expression.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)
        return function?.isOperator == true
    }
}
