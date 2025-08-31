package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLineInListRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-blank-lines-in-list) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "1.23.0")
class NoBlankLineInList(config: Config) : KtlintRule(
    config,
    "Disallow blank lines in lists before, between or after any list element."
) {

    override val wrapping = NoBlankLineInListRule()
}
