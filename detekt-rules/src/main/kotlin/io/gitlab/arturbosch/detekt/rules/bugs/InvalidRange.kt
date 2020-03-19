package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.getIntValueForPsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression

/**
 * Reports ranges which are empty.
 * This might be a bug if it is used for instance as a loop condition. This loop will never be triggered then.
 * This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.
 *
 * <noncompliant>
 * for (i in 2..1) {}
 * for (i in 1 downTo 2) {}
 *
 * val range1 = 2 until 1
 * val range2 = 2 until 2
 * </noncompliant>
 *
 * <compliant>
 * for (i in 2..2) {}
 * for (i in 2 downTo 2) {}
 *
 * val range = 2 until 3
 * </compliant>
 *
 * @active since v1.2.0
 */
class InvalidRange(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Defect,
            "If a for loops condition is false before the first iteration, the loop will never get executed.",
            Debt.TEN_MINS)

    private val minimumSize = 3

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        val range = expression.children
        if (range.size >= minimumSize && hasInvalidLoopRange(range)) {
            report(CodeSmell(issue, Entity.from(expression),
                    "This loop will never be executed due to its expression."))
        }
        super.visitBinaryExpression(expression)
    }

    private fun hasInvalidLoopRange(range: Array<PsiElement>): Boolean {
        val lowerValue = getIntValueForPsiElement(range[0])
        val upperValue = getIntValueForPsiElement(range[2])
        if (lowerValue == null || upperValue == null) {
            return false
        }
        return when (range[1].text) {
            ".." -> checkRangeTo(lowerValue, upperValue)
            "downTo" -> checkDownTo(lowerValue, upperValue)
            "until" -> checkUntil(lowerValue, upperValue)
            else -> false
        }
    }

    private fun checkRangeTo(lower: Int, upper: Int) = lower > upper

    private fun checkDownTo(lower: Int, upper: Int) = lower < upper

    private fun checkUntil(lower: Int, upper: Int) = lower >= upper
}
