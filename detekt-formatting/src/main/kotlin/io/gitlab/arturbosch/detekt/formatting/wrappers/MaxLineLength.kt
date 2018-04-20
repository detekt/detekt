package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.MaxLineLengthRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class MaxLineLength(config: Config) : FormattingRule(config) {

	override val wrapping = MaxLineLengthRule()
	override val issue = issueFor("Reports lines with exceeded length")
}
