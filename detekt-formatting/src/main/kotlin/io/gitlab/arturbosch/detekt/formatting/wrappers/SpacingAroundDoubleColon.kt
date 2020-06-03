package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingAroundDoubleColonRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.10.0
 */
class SpacingAroundDoubleColon(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundDoubleColonRule()
    override val issue = issueFor("Reports spaces around double colons")
}
