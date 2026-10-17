package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundDotRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#dot-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class SpacingAroundDot(config: Config) :
    KtlintRule(config, "Reports spaces around member invocation operator (dot)."),
    AutoCorrectable {

    override val wrapping = SpacingAroundDotRule()
}
