package io.gitlab.arturbosch.detekt.rules.bugs

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
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
class UnconditionalJumpStatementInLoop(config: Config) : Rule(
    config,
    "An unconditional jump statement in a loop is useless. The loop itself is only executed once."
) {

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        if (loopExpression.hasJumpStatements((loopExpression.parent as? KtLabeledExpression)?.getLabelName())) {
            report(
                Finding(
                    Entity.from(loopExpression),
                    "This loop contains an unconditional " +
                        "jump expression which " +
                        "essentially renders it useless as it will exit the loop during the first iteration."
                )
            )
        }
        super.visitLoopExpression(loopExpression)
    }

    private fun KtLoopExpression.hasJumpStatements(label: String?): Boolean {
        val body = this.body ?: return false
        return when (body) {
            is KtBlockExpression -> {
                body.children.takeWhile {
                    if (label != null) {
                        val labelExpression = it.findDescendantOfType<KtContinueExpression>()
                        labelExpression == null || labelExpression.getLabelName() != label
                    } else {
                        true
                    }
                }.any { it.isJumpStatement() }
            }

            else -> {
                body.isJumpStatement()
            }
        }
    }

    private fun PsiElement.isJumpStatement(): Boolean =
        this is KtReturnExpression &&
            !isFollowedByElvisJump() &&
            !isAfterConditionalJumpStatement() ||
            this is KtBreakExpression ||
            this is KtContinueExpression

    private fun KtReturnExpression.isFollowedByElvisJump(): Boolean =
        (returnedExpression as? KtBinaryExpression)?.isElvisJump() == true

    private fun KtBinaryExpression.isElvisJump(): Boolean =
        operationToken == KtTokens.ELVIS && (right is KtBreakExpression || right is KtContinueExpression)

    private fun PsiElement.isAfterConditionalJumpStatement(): Boolean =
        siblings(forward = false, withItself = false).any { it.isConditionalJumpStatement() }

    private fun PsiElement.isConditionalJumpStatement(): Boolean =
        anyDescendantOfType<PsiElement> { it.isJumpStatement() }
}
