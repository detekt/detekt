package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyFirstLineInMethodBlockRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-leading-empty-lines-in-method-blocks) for
 * documentation.
 */
@AutoCorrectable(since = "1.4.0")
@ActiveByDefault(since = "1.22.0")
class NoEmptyFirstLineInMethodBlock(config: Config) : FormattingRule(
    config,
    "Reports methods that have an empty first line."
) {

    override val wrapping = NoEmptyFirstLineInMethodBlockRule()
}
