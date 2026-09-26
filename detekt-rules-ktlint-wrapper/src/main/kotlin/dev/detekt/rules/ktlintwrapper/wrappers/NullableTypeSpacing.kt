package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.NullableTypeSpacingRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#nullable-type-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
internal class NullableTypeSpacing(config: Config) :
    KtlintRule(config, "Ensure no spaces in nullable type."),
    AutoCorrectable {

    override val wrapping = NullableTypeSpacingRule()
}
