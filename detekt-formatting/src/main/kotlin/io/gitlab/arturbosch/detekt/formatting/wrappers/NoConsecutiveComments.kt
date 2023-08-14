package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoConsecutiveCommentsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#disallow-consecutive-comments) for documentation.
 */
class NoConsecutiveComments(config: Config) : FormattingRule(config) {

    override val wrapping = NoConsecutiveCommentsRule()
    override val issue = issueFor("Disallow consecutive comments in most cases.")
}
