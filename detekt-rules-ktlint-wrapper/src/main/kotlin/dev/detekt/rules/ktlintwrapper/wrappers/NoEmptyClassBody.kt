package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.NoEmptyClassBodyRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-empty-class-bodies) for documentation.
 *
 * This rules overlaps with [empty-blocks>EmptyClassBlock](https://detekt.dev/empty-blocks.html#emptyclassblock)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoEmptyClassBody(config: Config) :
    KtlintRule(config, "Reports empty class bodies"),
    AutoCorrectable {

    override val wrapping = NoEmptyClassBodyRule()
}
