package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyClassBodyRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-empty-class-bodies) for documentation.
 *
 * This rules overlaps with [empty-blocks>EmptyClassBlock](https://detekt.dev/empty-blocks.html#emptyclassblock)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoEmptyClassBody(config: Config) : FormattingRule(
    config,
    "Reports empty class bodies"
) {

    override val wrapping = NoEmptyClassBodyRule()
}
