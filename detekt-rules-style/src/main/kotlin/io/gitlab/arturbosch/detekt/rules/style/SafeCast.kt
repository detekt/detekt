package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * This rule inspects casts and reports casts which could be replaced with safe casts instead.
 *
 * <noncompliant>
 * fun numberMagic(number: Number) {
 *     val i = if (number is Int) number else null
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun numberMagic(number: Number) {
 *     val i = number as? Int
 *     // ...
 * }
 * </compliant>
 *
 * @active since v1.0.0
 */
class SafeCast(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
            javaClass.simpleName,
            Severity.Style,
            "Safe cast instead of if-else-null",
            Debt.FIVE_MINS
    )

    override fun visitIfExpression(expression: KtIfExpression) {
        val condition = expression.condition
        if (condition is KtIsExpression) {
            val leftHandSide = condition.leftHandSide
            if (leftHandSide is KtNameReferenceExpression) {
                val identifier = leftHandSide.text
                val thenClause = expression.then
                val elseClause = expression.`else`
                val result = when (condition.isNegated) {
                    true -> isIfElseNull(elseClause, thenClause, identifier)
                    false -> isIfElseNull(thenClause, elseClause, identifier)
                }
                if (result) {
                    addReport(expression)
                }
            }
        }
    }

    private fun isIfElseNull(thenClause: KtExpression?, elseClause: KtExpression?, identifier: String): Boolean {
        val hasIdentifier = thenClause?.children?.firstOrNull()?.text == identifier
        val elseStatement = elseClause?.children?.firstOrNull()
        val hasNull = elseStatement is KtConstantExpression && elseStatement.node.elementType == KtNodeTypes.NULL
        return hasIdentifier && hasNull
    }

    private fun addReport(expression: KtIfExpression) {
        report(CodeSmell(issue, Entity.from(expression), "This cast should be replaced with a safe cast: as?"))
    }
}
