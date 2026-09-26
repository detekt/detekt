package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.ThenSpacingRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#then-spacing) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
internal class ThenSpacing(config: Config) :
    KtlintRule(config, "Enforces consistent spacing around the `then` block in an `if`-statement"),
    AutoCorrectable {

    override val wrapping = ThenSpacingRule()
}
