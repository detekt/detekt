package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.PropertyNamingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/__KTLINT_VERSION__/rules/experimental/#property-naming) for
 * documentation.
 */
class PropertyName(config: Config) : FormattingRule(config) {
    override val wrapping = PropertyNamingRule()
    override val issue =
        issueFor("Reports incorrect property name.")
}
