package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.
 *
 * Benchmarks have shown that using forEach on a range can have a huge performance cost in comparison to
 * simple for loops. Hence in most contexts a simple for loop should be used instead.
 * See more details here: https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 * To solve this CodeSmell, the forEach usage should be replaced by a for loop.
 *
 * <noncompliant>
 * (1..10).forEach {
 *     println(it)
 * }
 * </noncompliant>
 *
 * <compliant>
 * for (i in 1..10) {
 *     println(i)
 * }
 * </compliant>
 *
 * @active since v1.0.0
 *
 * @author Marvin Ramin
 * @author Ivan Balaksha
 * @author schalkms
 */
class ForEachOnRange(config: Config = Config.empty) : Rule(config) {
	override val issue = Issue("ForEachOnRange",
			Severity.Performance,
			"Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.",
			Debt.FIVE_MINS)

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)

		expression.getCallNameExpression()?.let {
			if (!it.textMatches("forEach")) {
				return
			}

			it.getReceiverExpression()?.text?.let {
				if (it matches rangeRegex) {
					report(CodeSmell(issue, Entity.from(expression), issue.description))
				}
			}
		}
	}

	companion object {
		val rangeRegex = Regex("\\(.*\\.\\..+\\)")
	}
}
