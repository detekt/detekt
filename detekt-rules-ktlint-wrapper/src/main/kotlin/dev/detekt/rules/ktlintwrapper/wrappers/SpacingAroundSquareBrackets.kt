package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingAroundSquareBracketsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#square-brackets-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "2.0.0")
@ActiveByDefault(since = "2.0.0")
class SpacingAroundSquareBrackets(config: Config) : KtlintRule(config, "Reports spaces around square brackets") {

    override val wrapping = SpacingAroundSquareBracketsRule()
}
