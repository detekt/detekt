package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * This rule set provides rules that address formatting issues.
 *
 * @configuration android - if android style guides should be preferred (default: false)
 * @configuration autoCorrect - if rules should auto correct style violation (default: true)
 * @active since v1.0.0
 * @author Artur Bosch
 */
class FormattingProvider : RuleSetProvider {

	override val ruleSetId: String = "formatting"

	override fun instance(config: Config) =
			RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))
}
