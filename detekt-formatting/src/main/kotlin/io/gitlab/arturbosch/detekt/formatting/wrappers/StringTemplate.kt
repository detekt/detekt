package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.StringTemplateRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io for documentation.
 *
 * @author Artur Bosch
 */
class StringTemplate(config: Config) : FormattingRule(config) {

	override val wrapping = StringTemplateRule()
	override val issue = issueFor("Detects simplifications in template strings")
}
