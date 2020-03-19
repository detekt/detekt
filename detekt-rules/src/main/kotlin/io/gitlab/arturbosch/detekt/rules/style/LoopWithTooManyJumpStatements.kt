package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLoopExpression

/**
 * Loops which contain multiple `break` or `continue` statements are hard to read and understand.
 * To increase readability they should be refactored into simpler loops.
 *
 * <noncompliant>
 * val strs = listOf("foo, bar")
 * for (str in strs) {
 *     if (str == "bar") {
 *         break
 *     } else {
 *         continue
 *     }
 * }
 * </noncompliant>
 *
 * @configuration maxJumpCount - maximum allowed jumps in a loop (default: `1`)
 *
 * @active since v1.2.0
 */
class LoopWithTooManyJumpStatements(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "The loop contains more than one break or continue statement. " +
                    "The code should be refactored to increase readability.", Debt.TEN_MINS)

    private val maxJumpCount = valueOrDefault(MAX_JUMP_COUNT, 1)

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        if (countBreakAndReturnStatements(loopExpression.body) > maxJumpCount) {
            report(CodeSmell(issue, Entity.from(loopExpression), issue.description))
        }
        super.visitLoopExpression(loopExpression)
    }

    private fun countBreakAndReturnStatements(body: KtExpression?) = body?.countBreakAndReturnStatementsInLoop() ?: 0

    private fun KtElement.countBreakAndReturnStatementsInLoop(): Int {
        var count = 0
        this.accept(object : DetektVisitor() {
            override fun visitKtElement(element: KtElement) {
                if (element is KtLoopExpression) {
                    return
                }
                if (element is KtBreakExpression || element is KtContinueExpression) {
                    count++
                }
                element.children.forEach { it.accept(this) }
            }
        })
        return count
    }

    companion object {
        const val MAX_JUMP_COUNT = "maxJumpCount"
    }
}
