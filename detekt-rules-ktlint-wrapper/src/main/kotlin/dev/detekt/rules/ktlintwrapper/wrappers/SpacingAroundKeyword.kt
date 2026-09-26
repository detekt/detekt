package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.SpacingAroundKeywordRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#keyword-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class SpacingAroundKeyword(config: Config) :
    KtlintRule(config, "Reports spaces around keywords"),
    AutoCorrectable {

    override val wrapping = SpacingAroundKeywordRule()
}
