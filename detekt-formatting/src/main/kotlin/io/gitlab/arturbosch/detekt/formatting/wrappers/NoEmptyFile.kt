package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyFileRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-empty-file) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class NoEmptyFile(config: Config) : FormattingRule(config) {

    override val wrapping = NoEmptyFileRule()
    override val issue = issueFor("Kotlin files must contain at least one declaration")
}
