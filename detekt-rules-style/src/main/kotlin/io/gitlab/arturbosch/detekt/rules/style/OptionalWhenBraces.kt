package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.hasCommentInside
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * This rule reports unnecessary braces in when expressions. These optional braces should be removed.
 *
 * <noncompliant>
 * val i = 1
 * when (i) {
 *     1 -> { println("one") } // unnecessary curly braces since there is only one statement
 *     else -> println("else")
 * }
 * </noncompliant>
 *
 * <compliant>
 * val i = 1
 * when (i) {
 *     1 -> println("one")
 *     else -> println("else")
 * }
 * </compliant>
 */
class OptionalWhenBraces(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Optional braces in when expression",
            Debt.FIVE_MINS)

    override fun visitWhenExpression(expression: KtWhenExpression) {
        for (entry in expression.entries) {
            val blockExpression = entry.expression as? KtBlockExpression
            if (blockExpression?.hasUnnecessaryBraces() == true) {
                report(CodeSmell(issue, Entity.from(entry), issue.description))
            }
        }
    }

    private fun KtBlockExpression.hasUnnecessaryBraces(): Boolean {
        val singleStatement = statements.singleOrNull()?.takeIf {
            !(it is KtLambdaExpression && it.functionLiteral.arrow == null)
        }
        return singleStatement != null && !hasCommentInside() && lBrace != null && rBrace != null
    }
}
