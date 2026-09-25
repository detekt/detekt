package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundSquareBracketsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/experimental/#square-brackets-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
internal class SpacingAroundSquareBrackets(config: Config) :
    KtlintRule(config, "Reports spaces around square brackets"),
    AutoCorrectable {

    override val wrapping = SpacingAroundSquareBracketsRule()
}
