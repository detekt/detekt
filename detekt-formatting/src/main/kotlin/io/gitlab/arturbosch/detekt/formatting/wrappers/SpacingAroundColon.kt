package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundColonRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io/#rule-spacing for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class SpacingAroundColon(config: Config) : FormattingRule(config) {

	override val wrapping = SpacingAroundColonRule()
	override val issue = issueFor("Reports spaces around colons")
}
