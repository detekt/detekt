package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * This rule detects `if` statements which can be collapsed. This can reduce nesting and help improve readability.
 *
 * However it should be carefully considered if merging the if statements actually does improve readability or if it
 * hides some edge-cases from the reader.
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
class CollapsibleIfStatements(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("CollapsibleIfStatements", Severity.Style,
            "Two if statements which could be collapsed were detected. " +
                    "These statements can be merged to improve readability.",
            Debt.FIVE_MINS)

    override fun visitIfExpression(expression: KtIfExpression) {
        if (isNotElseIfOrElse(expression) && hasOneKtIfExpression(expression)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
        super.visitIfExpression(expression)
    }

    private fun isNotElseIfOrElse(expression: KtIfExpression) =
            expression.`else` == null && expression.parent !is KtContainerNodeForControlStructureBody

    private fun hasOneKtIfExpression(expression: KtIfExpression): Boolean {
        val statement = expression.then?.getChildrenOfType<KtExpression>()?.singleOrNull()
        return statement != null && isLoneIfExpression(statement)
    }

    private fun isLoneIfExpression(statement: PsiElement): Boolean {
        val ifExpression = statement as? KtIfExpression
        return ifExpression != null && ifExpression.`else` == null
    }
}
