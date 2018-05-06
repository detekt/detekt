package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.FilenameRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class Filename(config: Config) : FormattingRule(config) {

	override val wrapping = FilenameRule()
	override val issue = issueFor("Checks if top level class matches the filename")
}
