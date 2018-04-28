package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundCurlyRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io/#rule-spacing for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class SpacingAroundCurly(config: Config) : FormattingRule(config) {

	override val wrapping = SpacingAroundCurlyRule()
	override val issue = issueFor("Reports spaces around curly braces")
}
