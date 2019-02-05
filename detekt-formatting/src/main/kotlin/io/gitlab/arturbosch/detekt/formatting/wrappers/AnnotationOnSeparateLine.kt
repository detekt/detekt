package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.AnnotationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class AnnotationOnSeparateLine(config: Config) : FormattingRule(config) {

	override val wrapping = AnnotationRule()
	override val issue = issueFor("Multiple annotations should be placed on separate lines. ")
}
