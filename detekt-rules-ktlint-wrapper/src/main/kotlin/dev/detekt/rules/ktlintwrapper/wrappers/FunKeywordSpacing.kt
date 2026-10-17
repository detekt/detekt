package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunKeywordSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#fun-keyword-spacing) for documentation.
 */
@ActiveByDefault(since = "1.23.0")
internal class FunKeywordSpacing(config: Config) :
    KtlintRule(config, "Checks the spacing after the fun keyword."),
    AutoCorrectable {

    override val wrapping = FunKeywordSpacingRule()
}
