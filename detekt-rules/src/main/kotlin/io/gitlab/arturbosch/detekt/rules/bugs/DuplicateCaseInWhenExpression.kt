package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenExpression(config: Config) : Rule(config) {

	override val issue = Issue("DuplicateCaseInWhenExpression",
			Severity.Warning,
			"Duplicated case statements in when expression. " +
					"Both cases should be merged.",
			Dept.TEN_MINS)

	override fun visitWhenExpression(expression: KtWhenExpression) {
		val numberOfEntries = expression.entries.size
		val distinctNumber = expression.entries
				.map { it.conditions }
				.fold(mutableListOf<String>(), { state, conditions ->
					state.apply { add(conditions.joinToString { it.text }) }
				})
				.distinct().size

		if (numberOfEntries > distinctNumber) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}
}
