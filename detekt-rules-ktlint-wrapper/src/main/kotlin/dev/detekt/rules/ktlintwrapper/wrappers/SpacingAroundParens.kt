package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundParensRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#parenthesis-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class SpacingAroundParens(config: Config) :
    KtlintRule(config, "Reports spaces around parentheses"),
    AutoCorrectable {

    override val wrapping = SpacingAroundParensRule()
}
