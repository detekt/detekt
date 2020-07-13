package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

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

    override val issue = Issue(javaClass.simpleName, Severity.Defect,
        "An unconditional jump statement in a loop is useless. " +
            "The loop itself is only executed once.", Debt.TEN_MINS)

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        if (hasJumpStatement(loopExpression.body)) {
            report(CodeSmell(issue, Entity.from(loopExpression), "This loop contains an unconditional " +
                "jump expression which " +
                "essentially renders it useless as it will exit the loop during the first iteration."))
        }
        super.visitLoopExpression(loopExpression)
    }

    private fun hasJumpStatement(body: KtExpression?): Boolean =
        body.isJumpStatement() || body?.children?.any { it.isJumpStatement() } == true

    private fun PsiElement?.isJumpStatement() =
        this is KtReturnExpression && !containsElvisContinue() ||
            this is KtBreakExpression || this is KtContinueExpression

    private fun KtReturnExpression.containsElvisContinue(): Boolean {
        val expr = this.returnedExpression
        return expr is KtBinaryExpression && expr.operationToken == KtTokens.ELVIS && expr.right is KtContinueExpression
    }
}
