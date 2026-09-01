package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoLineBreakAfterElseRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-line-break-after-else) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoLineBreakAfterElse(config: Config) :
    KtlintRule(config, "Reports line breaks after else"),
    AutoCorrectable {

    override val wrapping = NoLineBreakAfterElseRule()
}
