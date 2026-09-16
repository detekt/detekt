package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundRangeOperatorRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#range-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class SpacingAroundRangeOperator(config: Config) :
    KtlintRule(config, "Reports spaces around range operator"),
    AutoCorrectable {

    override val wrapping = SpacingAroundRangeOperatorRule()
}
