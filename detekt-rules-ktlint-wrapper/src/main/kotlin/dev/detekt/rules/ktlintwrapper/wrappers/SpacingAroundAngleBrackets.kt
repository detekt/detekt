package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundAngleBracketsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#angle-bracket-spacing) for documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class SpacingAroundAngleBrackets(config: Config) :
    KtlintRule(config, "Reports spaces around angle brackets"),
    AutoCorrectable {

    override val wrapping = SpacingAroundAngleBracketsRule()
}
