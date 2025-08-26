package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLineBeforeRbraceRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-blank-lines-before) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoBlankLineBeforeRbrace(config: Config) : FormattingRule(
    config,
    "Detects blank lines before rbraces"
) {

    override val wrapping = NoBlankLineBeforeRbraceRule()
}
