package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.getPossiblyQualifiedCallExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
    RequiresTypeResolution {
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
        report(CodeSmell(Entity.from(conditionCalleeExpression), message))
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
        val descriptor = getResolvedCall(bindingContext)?.resultingDescriptor ?: return null
        val receiverParameter = descriptor.dispatchReceiverParameter ?: descriptor.extensionReceiverParameter
        val receiverType = receiverParameter?.type ?: return null
        if (KotlinBuiltIns.isArrayOrPrimitiveArray(receiverType)) return null
        val conditionCallFqName = descriptor.fqNameOrNull() ?: return null
        return replacements[conditionCallFqName]
    }

    private data class Replacement(
        val conditionFunctionFqName: FqName,
        val replacementFunctionName: String,
        val negativeCondition: Boolean = false
    )

    companion object {
        private const val IF_BLANK = "ifBlank"

        private const val IF_EMPTY = "ifEmpty"

        private val replacements = listOf(
            Replacement(FqName("kotlin.text.isBlank"), IF_BLANK),
            Replacement(FqName("kotlin.text.isEmpty"), IF_EMPTY),
            Replacement(FqName("kotlin.collections.List.isEmpty"), IF_EMPTY),
            Replacement(FqName("kotlin.collections.Set.isEmpty"), IF_EMPTY),
            Replacement(FqName("kotlin.collections.Map.isEmpty"), IF_EMPTY),
            Replacement(FqName("kotlin.collections.Collection.isEmpty"), IF_EMPTY),
            Replacement(FqName("kotlin.text.isNotBlank"), IF_BLANK, negativeCondition = true),
            Replacement(FqName("kotlin.text.isNotEmpty"), IF_EMPTY, negativeCondition = true),
            Replacement(FqName("kotlin.collections.isNotEmpty"), IF_EMPTY, negativeCondition = true),
            Replacement(FqName("kotlin.String.isEmpty"), IF_EMPTY)
        ).associateBy { it.conditionFunctionFqName }

        private val conditionFunctionShortNames = replacements.keys.map { it.shortName().asString() }.toSet()
    }
}
