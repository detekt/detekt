package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.countDescendantsBy
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLoopExpression

class LoopWithTooManyJumpStatements(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"The loop contains more than one break or continue statement. " +
					"The code should be refactored to increase readability.", Debt.TEN_MINS)

	override fun visitLoopExpression(loopExpression: KtLoopExpression) {
		if (countBreakAndReturnStatements(loopExpression.body) > 1) {
			report(CodeSmell(issue, Entity.from(loopExpression)))
		}
		super.visitLoopExpression(loopExpression)
	}

	private fun countBreakAndReturnStatements(body: KtExpression?): Int {
		if (body == null) {
			return 0
		}
		return body.countDescendantsBy { it is KtBreakExpression || it is KtContinueExpression }
	}
}
