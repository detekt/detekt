package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * This rule set provides rules that address formatting issues.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class FormattingProvider : RuleSetProvider {

	override val ruleSetId: String = "formatting"

	override fun instance(config: Config) =
			RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))
}
