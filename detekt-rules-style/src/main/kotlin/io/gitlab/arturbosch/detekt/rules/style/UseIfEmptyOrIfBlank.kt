package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
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
 * @requiresTypeResolution
 */
@Suppress("ReturnCount", "ComplexMethod")
class UseIfEmptyOrIfBlank(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "UseIfEmptyOrIfBlank",
        Severity.Style,
        "Use 'ifEmpty' or 'ifBlank' instead of 'isEmpty' or 'isBlank' to assign default value.",
        Debt.FIVE_MINS
    )

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        if (expression.isElseIf()) return
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
        report(CodeSmell(issue, Entity.from(conditionCalleeExpression), message))
    }

    private fun KtExpression.isElseIf(): Boolean = parent.node.elementType == KtNodeTypes.ELSE

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
        private const val ifBlank = "ifBlank"

        private const val ifEmpty = "ifEmpty"

        private val replacements = listOf(
            Replacement(FqName("kotlin.text.isBlank"), ifBlank),
            Replacement(FqName("kotlin.text.isEmpty"), ifEmpty),
            Replacement(FqName("kotlin.collections.List.isEmpty"), ifEmpty),
            Replacement(FqName("kotlin.collections.Set.isEmpty"), ifEmpty),
            Replacement(FqName("kotlin.collections.Map.isEmpty"), ifEmpty),
            Replacement(FqName("kotlin.collections.Collection.isEmpty"), ifEmpty),
            Replacement(FqName("kotlin.text.isNotBlank"), ifBlank, negativeCondition = true),
            Replacement(FqName("kotlin.text.isNotEmpty"), ifEmpty, negativeCondition = true),
            Replacement(FqName("kotlin.collections.isNotEmpty"), ifEmpty, negativeCondition = true),
            Replacement(FqName("kotlin.String.isEmpty"), ifEmpty)
        ).associateBy { it.conditionFunctionFqName }

        private val conditionFunctionShortNames = replacements.keys.map { it.shortName().asString() }.toSet()
    }
}
