package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenExpression(config: Config) : Rule("DuplicateCaseInWhenExpression", Severity.Defect, config) {

	override fun visitWhenExpression(expression: KtWhenExpression) {
		val numberOfEntries = expression.entries.size
		val distinctNumber = expression.entries
				.map { it.conditions }
				.fold(mutableListOf<String>(), { state, conditions ->
					state.apply { add(conditions.joinToString { it.text }) }
				})
				.distinct().size

		if (numberOfEntries > distinctNumber) {
			addFindings(CodeSmell(id, Entity.from(expression)))
		}
	}
}