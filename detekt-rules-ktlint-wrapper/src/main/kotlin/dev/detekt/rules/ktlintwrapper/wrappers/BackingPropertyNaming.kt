package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BackingPropertyNamingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#backing-property-naming)
 * for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class BackingPropertyNaming(config: Config) : KtlintRule(
    config,
    "Reports incorrect property name."
) {
    override val wrapping = BackingPropertyNamingRule()
}
