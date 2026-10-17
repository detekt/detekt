package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoUnitReturnRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-unit-as-return-type) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoUnitReturn(config: Config) :
    KtlintRule(config, "Detects optional 'Unit' return types"),
    AutoCorrectable {

    override val wrapping = NoUnitReturnRule()
}
