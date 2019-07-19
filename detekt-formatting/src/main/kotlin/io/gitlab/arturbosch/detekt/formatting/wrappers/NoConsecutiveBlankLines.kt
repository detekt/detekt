package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoConsecutiveBlankLinesRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-blank">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class NoConsecutiveBlankLines(config: Config) : FormattingRule(config) {

    override val wrapping = NoConsecutiveBlankLinesRule()
    override val issue = issueFor("Reports consecutive blank lines")
}
