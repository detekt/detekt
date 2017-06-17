package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenExpression(config: Config) : Rule("DuplicateCaseInWhenExpression", config) {

	override fun visitWhenExpression(context: Context, expression: KtWhenExpression) {
		val numberOfEntries = expression.entries.size
		val distinctNumber = expression.entries
				.map { it.conditions }
				.fold(mutableListOf<String>(), { state, conditions ->
					state.apply { add(conditions.joinToString { it.text }) }
				})
				.distinct().size

		if (numberOfEntries > distinctNumber) {
			context.report(CodeSmell(ISSUE, Entity.from(expression)))
		}
	}

	companion object {
		val ISSUE = Issue("DuplicateCaseInWhenExpression", Issue.Severity.Defect)
	}
}