package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundDoubleColonRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#double-colon-spacing) for documentation.
 */
@AutoCorrectable(since = "1.10.0")
@ActiveByDefault(since = "1.22.0")
class SpacingAroundDoubleColon(config: Config) : FormattingRule(
    config,
    "Reports spaces around double colons"
) {

    override val wrapping = SpacingAroundDoubleColonRule()
}
