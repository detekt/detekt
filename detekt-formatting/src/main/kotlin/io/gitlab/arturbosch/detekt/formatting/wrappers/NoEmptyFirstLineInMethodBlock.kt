package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoEmptyFirstLineInMethodBlockRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#no-leading-empty-lines-in-method-blocks) for
 * documentation.
 */
@AutoCorrectable(since = "1.4.0")
@ActiveByDefault(since = "1.22.0")
class NoEmptyFirstLineInMethodBlock(config: Config) : FormattingRule(config) {

    override val wrapping = NoEmptyFirstLineInMethodBlockRule()
    override val issue = issueFor("Reports methods that have an empty first line.")
}
