package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundSquareBracketsRule
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#square-brackets-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "2.0.0")
class SpacingAroundSquareBrackets(config: Config) : FormattingRule(
    config,
    "Reports spaces around square brackets"
) {

    override val wrapping = SpacingAroundSquareBracketsRule()
}
