package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.ChainWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class ChainWrapping(config: Config) : FormattingRule(config) {

	override val wrapping = ChainWrappingRule()
	override val issue = issueFor("Checks if condition chaining is wrapped right")
}
