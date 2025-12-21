package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunKeywordSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#fun-keyword-spacing) for documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class FunKeywordSpacing(config: Config) : KtlintRule(config, "Checks the spacing after the fun keyword.") {

    override val wrapping = FunKeywordSpacingRule()
}
