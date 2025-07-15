package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * This rule detects `if` statements which can be collapsed. This can reduce nesting and help improve readability.
 *
 * However, carefully consider whether merging the if statements actually improves readability, as collapsing the
 * statements may hide some edge cases from the reader.
 *
 * <noncompliant>
 * val i = 1
 * if (i > 0) {
 *     if (i < 5) {
 *         println(i)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * val i = 1
 * if (i > 0 && i < 5) {
 *     println(i)
 * }
 * </compliant>
 */
class CollapsibleIfStatements(config: Config) : Rule(
    config,
    "Two if statements which could be collapsed were detected. " +
        "These statements can be merged to improve readability."
) {

    override fun visitIfExpression(expression: KtIfExpression) {
        if (isNotElseIfOrElse(expression) && hasOneKtIfExpression(expression)) {
            report(Finding(Entity.from(expression), description))
        }
        super.visitIfExpression(expression)
    }

    private fun isNotElseIfOrElse(expression: KtIfExpression) =
        expression.`else` == null && expression.parent !is KtContainerNodeForControlStructureBody

    private fun hasOneKtIfExpression(expression: KtIfExpression) =
        expression.then?.getChildrenOfType<KtExpression>()?.singleOrNull().isLoneIfExpression()

    private fun KtExpression?.isLoneIfExpression() = this is KtIfExpression && `else` == null
}
