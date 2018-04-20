package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoSemicolonsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class NoSemicolons(config: Config) : FormattingRule(config) {

	override val wrapping = NoSemicolonsRule()
	override val issue = issueFor("Detects semicolons")
}
