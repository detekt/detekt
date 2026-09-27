package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.SpacingAroundRangeOperatorRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#range-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class SpacingAroundRangeOperator(config: Config) :
    KtlintRule(config, "Reports spaces around range operator"),
    AutoCorrectable {

    override val wrapping = SpacingAroundRangeOperatorRule()
}
