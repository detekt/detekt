package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLineInListRule
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-blank-lines-in-list) for documentation.
 */
internal class NoBlankLineInList(config: Config) :
    KtlintRule(config, "Disallow blank lines in lists before, between or after any list element."),
    AutoCorrectable {

    override val wrapping = NoBlankLineInListRule()
}
