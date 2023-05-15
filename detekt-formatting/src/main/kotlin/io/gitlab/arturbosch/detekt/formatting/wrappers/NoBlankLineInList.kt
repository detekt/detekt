package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLineInListRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/__KTLINT_VERSION__/rules/experimental/#no-blank-lines-in-list) for documentation.
 */
@AutoCorrectable(since = "1.23.0")
class NoBlankLineInList(config: Config) : FormattingRule(config) {

    override val wrapping = NoBlankLineInListRule()
    override val issue =
        issueFor("Disallow blank lines in lists before, between or after any list element.")
}
