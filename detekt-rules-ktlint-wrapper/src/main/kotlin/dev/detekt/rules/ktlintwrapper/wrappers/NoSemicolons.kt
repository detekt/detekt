package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoSemicolonsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-semicolons) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoSemicolons(config: Config) :
    KtlintRule(config, "Detects semicolons"),
    AutoCorrectable {

    override val wrapping = NoSemicolonsRule()
}
