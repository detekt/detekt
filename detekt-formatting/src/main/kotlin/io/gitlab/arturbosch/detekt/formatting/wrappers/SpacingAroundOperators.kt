package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundOperatorsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io/#rule-spacing for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class SpacingAroundOperators(config: Config) : FormattingRule(config) {

	override val wrapping = SpacingAroundOperatorsRule()
	override val issue = issueFor("Reports spaces around operators")
}
