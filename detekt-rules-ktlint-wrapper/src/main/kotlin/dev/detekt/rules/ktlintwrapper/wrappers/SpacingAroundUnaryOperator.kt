package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundUnaryOperatorRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#unary-operator-spacing) for documentation.
 */
@AutoCorrectable(since = "1.16.0")
@ActiveByDefault(since = "1.22.0")
class SpacingAroundUnaryOperator(config: Config) : KtlintRule(
    config,
    "Reports spaces around unary operator"
) {

    override val wrapping = SpacingAroundUnaryOperatorRule()
}
