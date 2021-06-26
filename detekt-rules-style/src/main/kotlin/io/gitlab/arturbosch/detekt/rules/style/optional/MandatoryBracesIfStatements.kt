package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

private const val DESCRIPTION = "Multi-line if statement was found that does not have braces. " +
    "These should be added to improve readability."

/**
 * This rule detects multi-line `if` statements which do not have braces.
 * Adding braces would improve readability and avoid possible errors.
 *
 * <noncompliant>
 * val i = 1
 * if (i > 0)
 *    println(i)
 * </noncompliant>
 *
 * <compliant>
 * val x = if (condition) 5 else 4
 * </compliant>
 */
class MandatoryBracesIfStatements(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("MandatoryBracesIfStatements", Severity.Style, DESCRIPTION, Debt.FIVE_MINS)

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)

        val thenExpression = expression.then ?: return
        if (thenExpression !is KtBlockExpression && hasNewLineAfter(expression.rightParenthesis)) {
            report(CodeSmell(issue, Entity.from(thenExpression), DESCRIPTION))
        }

        val elseExpression = expression.`else` ?: return
        if (mustBeOnSameLine(elseExpression) && hasNewLineAfter(expression.elseKeyword)) {
            report(CodeSmell(issue, Entity.from(elseExpression), DESCRIPTION))
        }
    }

    private fun hasNewLineAfter(element: PsiElement?): Boolean {
        if (element == null) return false
        return element
            .siblings(forward = true, withItself = false)
            .takeWhile { it.text != "else" }
            .any { it.textContains('\n') }
    }

    private fun mustBeOnSameLine(expression: KtExpression): Boolean =
        expression !is KtIfExpression && expression !is KtBlockExpression && expression !is KtWhenExpression
}
