package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoConsecutiveCommentsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-consecutive-comments) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class NoConsecutiveComments(config: Config) : FormattingRule(
    config,
    "Disallow consecutive comments in most cases."
) {

    override val wrapping = NoConsecutiveCommentsRule()
}
