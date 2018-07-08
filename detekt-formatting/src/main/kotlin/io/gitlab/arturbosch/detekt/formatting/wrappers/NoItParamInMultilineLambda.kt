package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.github.shyiko.ktlint.ruleset.standard.NoItParamInMultilineLambdaRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 */
class NoItParamInMultilineLambda(config: Config) : FormattingRule(config) {

	override val wrapping = NoItParamInMultilineLambdaRule()
	override val issue = issueFor("Reports 'it' variable usages in multiline lambdas")
}
