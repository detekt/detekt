package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class FinalNewline(config: Config) : FormattingRule(config) {

	override val wrapping = FinalNewlineRule()
	override val issue = issueFor("Detects missing final newlines")
}
