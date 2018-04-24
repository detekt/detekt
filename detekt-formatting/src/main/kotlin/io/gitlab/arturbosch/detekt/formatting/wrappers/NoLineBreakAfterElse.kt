package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoLineBreakAfterElseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class NoLineBreakAfterElse(config: Config) : FormattingRule(config) {

	override val wrapping = NoLineBreakAfterElseRule()
	override val issue = issueFor("Reports line breaks after else")
}
