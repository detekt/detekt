package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoUnitReturnRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See https://ktlint.github.io/#rule-unit-return for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 * @author Artur Bosch
 */
class NoUnitReturn(config: Config) : FormattingRule(config) {

	override val wrapping = NoUnitReturnRule()
	override val issue = issueFor("Detects optional 'Unit' return types")
}
