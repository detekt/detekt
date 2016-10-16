package io.gitlab.arturbosch.detekt.default

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.NoElseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.WildcardImport

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {
	override fun instance(): RuleSet {
		return RuleSet("style", listOf(
				WildcardImport(),
				NoElseInWhenExpression()
		))
	}
}