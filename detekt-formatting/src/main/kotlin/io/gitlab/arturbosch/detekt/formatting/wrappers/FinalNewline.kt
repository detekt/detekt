package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class FinalNewline(config: Config) : FormattingRule(config) {

    override val wrapping = FinalNewlineRule()
    override val issue = issueFor("Detects missing final newlines")
}
