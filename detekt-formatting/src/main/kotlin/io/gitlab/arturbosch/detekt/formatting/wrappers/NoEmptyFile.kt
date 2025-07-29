package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyFileRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-empty-file) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class NoEmptyFile(config: Config) : FormattingRule(
    config,
    "Kotlin files must contain at least one declaration"
) {

    override val wrapping = NoEmptyFileRule()
}
