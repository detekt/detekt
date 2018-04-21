package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoMultipleSpacesRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class NoMultipleSpaces(config: Config) : FormattingRule(config) {

	override val wrapping = NoMultipleSpacesRule()
	override val issue = issueFor("Reports multiple space usages")
}
