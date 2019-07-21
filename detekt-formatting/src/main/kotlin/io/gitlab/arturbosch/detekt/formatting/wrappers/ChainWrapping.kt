package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.ChainWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class ChainWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = ChainWrappingRule()
    override val issue = issueFor("Checks if condition chaining is wrapped right")
}
