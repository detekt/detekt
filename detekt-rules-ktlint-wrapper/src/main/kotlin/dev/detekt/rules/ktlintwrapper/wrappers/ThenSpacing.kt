package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ThenSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#then-spacing) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "2.0.0")
class ThenSpacing(config: Config) : KtlintRule(
    config,
    "Enforces consistent spacing around the `then` block in an `if`-statement"
) {

    override val wrapping = ThenSpacingRule()
}
