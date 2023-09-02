package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * Reports loops which contain jump statements that jump regardless of any conditions.
 * This implies that the loop is only executed once and thus could be rewritten without a
 * loop altogether.
 *
 * <noncompliant>
 * for (i in 1..2) break
 * </noncompliant>
 *
 * <compliant>
 * for (i in 1..2) {
 *     if (i == 1) break
 * }
 * </compliant>
 */
class UnconditionalJumpStatementInLoop(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "An unconditional jump statement in a loop is useless. " +
            "The loop itself is only executed once.",
        Debt.TEN_MINS
    )

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        if (loopExpression.hasJumpStatements()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(loopExpression),
                    "This loop contains an unconditional " +
                        "jump expression which " +
                        "essentially renders it useless as it will exit the loop during the first iteration."
                )
            )
        }
        super.visitLoopExpression(loopExpression)
    }

    private fun KtLoopExpression.hasJumpStatements(): Boolean {
        val body = this.body ?: return false
        return when (body) {
            is KtBlockExpression -> body.children.any { it.isJumpStatement() }
            else -> body.isJumpStatement()
        }
    }

    private fun PsiElement.isJumpStatement(): Boolean =
        this is KtReturnExpression && !isFollowedByElvisJump() && !isAfterConditionalJumpStatement() ||
            this is KtBreakExpression || this is KtContinueExpression

    private fun KtReturnExpression.isFollowedByElvisJump(): Boolean =
        (returnedExpression as? KtBinaryExpression)?.isElvisJump() == true

    private fun KtBinaryExpression.isElvisJump(): Boolean =
        operationToken == KtTokens.ELVIS && (right is KtBreakExpression || right is KtContinueExpression)

    private fun PsiElement.isAfterConditionalJumpStatement(): Boolean =
        siblings(forward = false, withItself = false).any { it.isConditionalJumpStatement() }

    private fun PsiElement.isConditionalJumpStatement(): Boolean =
        anyDescendantOfType<PsiElement> { it.isJumpStatement() }
}
