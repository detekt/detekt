package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

private const val DESCRIPTION = "Multi-line loop was found that does not have braces. " +
        "These should be added to improve readability."

/**
 * This rule detects multi-line `for` and `while` loops which do not have braces.
 * Adding braces would improve readability and avoid possible errors.
 *
 * <noncompliant>
 * for (i in 0..10)
 *     println(i)
 *
 * while (true)
 *     println("Hello, world")
 * </noncompliant>
 *
 * <compliant>
 * for (i in 0..10) {
 *     println(i)
 * }
 *
 * for (i in 0..10) println(i)
 *
 * while (true) {
 *     println("Hello, world")
 * }
 *
 * while (true) println("Hello, world")
 * </compliant>
 */
class MandatoryBracesLoops(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue("MandatoryBracesLoops", Severity.Style, DESCRIPTION, Debt.FIVE_MINS)

    override fun visitForExpression(expression: KtForExpression) {
        checkForBraces(expression)
        super.visitForExpression(expression)
    }

    override fun visitWhileExpression(expression: KtWhileExpression) {
        checkForBraces(expression)
        super.visitWhileExpression(expression)
    }

    private fun checkForBraces(expression: KtLoopExpression) {
        // block expressions are okay if and only if it's a single line
        if (expression.isNotBlockExpression() && hasNewLine(expression.rightParenthesis)) {
            report(CodeSmell(issue, Entity.from(expression.body ?: expression), message = DESCRIPTION))
        }
    }

    private fun hasNewLine(element: PsiElement?): Boolean =
            element?.siblings(forward = true, withItself = false)
                    ?.map { println(it); it }
                    ?.filterIsInstance<PsiWhiteSpace>()
                    ?.firstOrNull { it.textContains('\n') } != null

    private fun KtLoopExpression.isNotBlockExpression(): Boolean = this.body !is KtBlockExpression
}
