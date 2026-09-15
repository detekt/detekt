package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyFirstLineInMethodBlockRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-leading-empty-lines-in-method-blocks) for
 * documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class NoEmptyFirstLineInMethodBlock(config: Config) :
    KtlintRule(config, "Reports methods that have an empty first line."),
    AutoCorrectable {

    override val wrapping = NoEmptyFirstLineInMethodBlockRule()
}
