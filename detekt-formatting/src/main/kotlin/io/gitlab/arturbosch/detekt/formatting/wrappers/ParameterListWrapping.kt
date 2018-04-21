package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.ParameterListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class ParameterListWrapping(config: Config) : FormattingRule(config) {

	override val wrapping = ParameterListWrappingRule()
	override val issue = issueFor("Detects mis-aligned parater lists")
}
