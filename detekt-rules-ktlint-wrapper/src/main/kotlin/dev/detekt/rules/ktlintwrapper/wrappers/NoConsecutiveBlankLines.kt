package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.NoConsecutiveBlankLinesRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-consecutive-blank-lines) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoConsecutiveBlankLines(config: Config) :
    KtlintRule(config, "Reports consecutive blank lines"),
    AutoCorrectable {

    override val wrapping = NoConsecutiveBlankLinesRule()
}
