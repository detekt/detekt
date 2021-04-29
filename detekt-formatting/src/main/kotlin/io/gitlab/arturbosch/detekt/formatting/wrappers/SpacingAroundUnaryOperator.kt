package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingAroundUnaryOperatorRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.16.0
 */
class SpacingAroundUnaryOperator(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundUnaryOperatorRule()
    override val issue = issueFor("Reports spaces around unary operator")
}
