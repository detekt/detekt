package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BackingPropertyNamingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#backing-property-naming)
 * for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class BackingPropertyNaming(config: Config) : FormattingRule(
    config,
    "Reports incorrect property name."
) {
    override val wrapping = BackingPropertyNamingRule()
}
