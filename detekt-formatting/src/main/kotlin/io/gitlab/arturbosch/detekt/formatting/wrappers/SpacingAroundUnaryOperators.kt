package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingAroundUnaryOperatorsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class SpacingAroundUnaryOperators(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundUnaryOperatorsRule()
    override val issue = issueFor("Reports spaces around unary operators.")
}
