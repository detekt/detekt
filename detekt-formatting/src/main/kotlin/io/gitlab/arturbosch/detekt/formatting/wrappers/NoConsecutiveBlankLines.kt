package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoConsecutiveBlankLinesRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class NoConsecutiveBlankLines(config: Config) : FormattingRule(config) {

	override val wrapping = NoConsecutiveBlankLinesRule()
	override val issue = issueFor("Reports consecutive blank lines")
}
