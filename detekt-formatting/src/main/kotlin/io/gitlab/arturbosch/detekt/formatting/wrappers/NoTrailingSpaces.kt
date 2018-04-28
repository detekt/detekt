package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoTrailingSpacesRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io/#rule-trailing-whitespaces for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class NoTrailingSpaces(config: Config) : FormattingRule(config) {

	override val wrapping = NoTrailingSpacesRule()
	override val issue = issueFor("Detects trailing spaces")
}
