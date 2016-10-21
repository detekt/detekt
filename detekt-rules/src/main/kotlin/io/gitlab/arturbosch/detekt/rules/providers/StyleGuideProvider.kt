package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.NoElseInWhenExpression
import io.gitlab.arturbosch.detekt.rules.UselessSemicolon
import io.gitlab.arturbosch.detekt.rules.WildcardImport

/**
 * @author Artur Bosch
 */
class StyleGuideProvider : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet("style", listOf(
				WildcardImport(config),
				NoElseInWhenExpression(config),
				UselessSemicolon(config)
		))
	}
}