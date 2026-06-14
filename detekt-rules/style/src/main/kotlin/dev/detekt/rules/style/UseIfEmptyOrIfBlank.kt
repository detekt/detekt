package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.receiverType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_COLLECTIONS_PACKAGE
import org.jetbrains.kotlin.name.StandardClassIds.BASE_TEXT_PACKAGE
import org.jetbrains.kotlin.name.StandardClassIds.Collection
import org.jetbrains.kotlin.name.StandardClassIds.List
import org.jetbrains.kotlin.name.StandardClassIds.Map
import org.jetbrains.kotlin.name.StandardClassIds.Set
import org.jetbrains.kotlin.name.StandardClassIds.String
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.getPossiblyQualifiedCallExpression

/**
 * This rule detects `isEmpty` or `isBlank` calls to assign a default value. They can be replaced with `ifEmpty` or
 * `ifBlank` calls.
 *
 * <noncompliant>
 * fun test(list: List<Int>, s: String) {
 *     val a = if (list.isEmpty()) listOf(1) else list
 *     val b = if (list.isNotEmpty()) list else listOf(2)
 *     val c = if (s.isBlank()) "foo" else s
 *     val d = if (s.isNotBlank()) s else "bar"
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test(list: List<Int>, s: String) {
 *     val a = list.ifEmpty { listOf(1) }
 *     val b = list.ifEmpty { listOf(2) }
 *     val c = s.ifBlank { "foo" }
 *     val d = s.ifBlank { "bar" }
 * }
 * </compliant>
 *
 */
class UseIfEmptyOrIfBlank(config: Config) :
    Rule(
        config,
        "Use `ifEmpty` or `ifBlank` instead of `isEmpty` or `isBlank` to assign a default value."
    ),
    RequiresAnalysisApi {

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)

        if (expression.parent.node.elementType == KtNodeTypes.ELSE) return
        val thenExpression = expression.then ?: return
        val elseExpression = expression.`else` ?: return
        if (elseExpression is KtIfExpression) return

        val (condition, isNegatedCondition) = expression.condition() ?: return
        val conditionCallExpression = condition.getPossiblyQualifiedCallExpression() ?: return
        val conditionCalleeExpression = conditionCallExpression.calleeExpression ?: return
        val conditionCalleeExpressionText = conditionCalleeExpression.text
        if (conditionCalleeExpressionText !in conditionFunctionShortNames) return

        val replacement = conditionCallExpression.replacement() ?: return
        val selfBranch = if (isNegatedCondition xor replacement.negativeCondition) thenExpression else elseExpression
        val selfValueExpression = selfBranch.blockExpressionsOrSingle().singleOrNull() ?: return
        if (condition is KtDotQualifiedExpression) {
            if (selfValueExpression.text != condition.receiverExpression.text) return
        } else if (selfValueExpression !is KtThisExpression) {
            return
        }

        val message =
            "This '$conditionCalleeExpressionText' call can be replaced with '${replacement.replacementFunctionName}'"
        report(Finding(Entity.from(conditionCalleeExpression), message))
    }

    @Suppress("ReturnCount")
    private fun KtIfExpression.condition(): Pair<KtExpression, Boolean>? {
        val condition = this.condition ?: return null
        return if (condition is KtPrefixExpression) {
            if (condition.operationToken != KtTokens.EXCL) return null
            val baseExpression = condition.baseExpression ?: return null
            baseExpression to true
        } else {
            condition to false
        }
    }

    @Suppress("ReturnCount")
    private fun KtCallExpression.replacement(): Replacement? {
        analyze(this) {
            val symbol = resolveToCall()?.singleFunctionCallOrNull()?.symbol ?: return null
            if (symbol.receiverType?.isArrayOrPrimitiveArray == true) return null
            return replacements[symbol.callableId]
        }
    }

    private data class Replacement(
        val conditionFunctionId: CallableId,
        val replacementFunctionName: String,
        val negativeCondition: Boolean = false,
    )

    @Suppress("NonBooleanPropertyPrefixedWithIs")
    companion object {
        private const val IF_BLANK = "ifBlank"
        private const val IF_EMPTY = "ifEmpty"

        private val isBlank = Name.identifier("isBlank")
        private val isEmpty = Name.identifier("isEmpty")
        private val isNotBlank = Name.identifier("isNotBlank")
        private val isNotEmpty = Name.identifier("isNotEmpty")

        private val replacements = listOf(
            Replacement(CallableId(BASE_TEXT_PACKAGE, isBlank), IF_BLANK),
            Replacement(CallableId(BASE_TEXT_PACKAGE, isEmpty), IF_EMPTY),
            Replacement(CallableId(List, isEmpty), IF_EMPTY),
            Replacement(CallableId(Set, isEmpty), IF_EMPTY),
            Replacement(CallableId(Map, isEmpty), IF_EMPTY),
            Replacement(CallableId(Collection, isEmpty), IF_EMPTY),
            Replacement(CallableId(BASE_TEXT_PACKAGE, isNotBlank), IF_BLANK, negativeCondition = true),
            Replacement(CallableId(BASE_TEXT_PACKAGE, isNotEmpty), IF_EMPTY, negativeCondition = true),
            Replacement(CallableId(BASE_COLLECTIONS_PACKAGE, isNotEmpty), IF_EMPTY, negativeCondition = true),
            Replacement(CallableId(String, isEmpty), IF_EMPTY)
        ).associateBy { it.conditionFunctionId }

        private val conditionFunctionShortNames = replacements.keys.map { it.callableName.asString() }.toSet()
    }
}
