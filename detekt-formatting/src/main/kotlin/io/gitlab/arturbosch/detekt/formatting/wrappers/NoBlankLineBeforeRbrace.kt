package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoBlankLineBeforeRbraceRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class NoBlankLineBeforeRbrace(config: Config) : FormattingRule(config) {

	override val wrapping = NoBlankLineBeforeRbraceRule()
	override val issue = issueFor("Detects blank lines before rbraces")
}
