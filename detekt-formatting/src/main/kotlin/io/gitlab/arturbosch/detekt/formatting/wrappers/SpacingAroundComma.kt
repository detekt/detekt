package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundCommaRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class SpacingAroundComma(config: Config) : FormattingRule(config) {

	override val wrapping = SpacingAroundCommaRule()
	override val issue = issueFor("Reports spaces around commas")
}
