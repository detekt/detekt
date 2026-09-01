package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundDoubleColonRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#double-colon-spacing) for documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class SpacingAroundDoubleColon(config: Config) :
    KtlintRule(config, "Reports spaces around double colons"),
    AutoCorrectable {

    override val wrapping = SpacingAroundDoubleColonRule()
}
