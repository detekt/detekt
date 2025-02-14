package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BackingPropertyNamingRule
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#backing-property-naming)
 * for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class BackingPropertyNaming(config: Config) : FormattingRule(
    config,
    "Reports incorrect property name."
) {
    override val wrapping = BackingPropertyNamingRule()
}
