package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

class ForEachOnRange(config: Config = Config.empty) : Rule(config) {
	override val issue = Issue("ForEachOnRange",
			Severity.Performance,
			"Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.")

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)

		expression.getCallNameExpression()?.let {
			if (!it.textMatches("forEach")) {
				return
			}

			it.getReceiverExpression()?.text?.let {
				if (it matches rangeRegex) {
					report(CodeSmell(issue, Entity.from(expression), message = ""))
				}
			}
		}
	}

	companion object {
		val rangeRegex = Regex("\\(.*\\.\\..+\\)")
	}
}
