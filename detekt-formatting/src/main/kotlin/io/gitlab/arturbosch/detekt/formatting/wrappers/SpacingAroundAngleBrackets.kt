package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundAngleBracketsRule
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#angle-bracket-spacing) for documentation.
 */
@AutoCorrectable(since = "1.16.0")
@ActiveByDefault(since = "1.22.0")
class SpacingAroundAngleBrackets(config: Config) : FormattingRule(
    config,
    "Reports spaces around angle brackets"
) {

    override val wrapping = SpacingAroundAngleBracketsRule()
}
