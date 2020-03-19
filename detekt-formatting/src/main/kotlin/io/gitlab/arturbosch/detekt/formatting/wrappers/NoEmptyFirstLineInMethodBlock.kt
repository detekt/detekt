package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.NoEmptyFirstLineInMethodBlockRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.4.0
 */
class NoEmptyFirstLineInMethodBlock(config: Config) : FormattingRule(config) {

    override val wrapping = NoEmptyFirstLineInMethodBlockRule()
    override val issue = issueFor("Reports methods that have an empty first line.")
}
