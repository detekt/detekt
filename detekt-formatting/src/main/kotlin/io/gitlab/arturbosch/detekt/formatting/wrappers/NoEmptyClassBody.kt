package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoEmptyClassBodyRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class NoEmptyClassBody(config: Config) : FormattingRule(config) {

	override val wrapping = NoEmptyClassBodyRule()
	override val issue = issueFor("Reports empty class bodies")
}
