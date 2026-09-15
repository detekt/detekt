package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLineBeforeRbraceRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-blank-lines-before) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoBlankLineBeforeRbrace(config: Config) :
    KtlintRule(config, "Detects blank lines before rbraces"),
    AutoCorrectable {

    override val wrapping = NoBlankLineBeforeRbraceRule()
}
