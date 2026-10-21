package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.BackingPropertyNamingRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#backing-property-naming)
 * for documentation.
 */
@ActiveByDefault(since = "2.0.0")
internal class BackingPropertyNaming(config: Config) : KtlintRule(config, "Reports incorrect property name.") {
    override val wrapping = BackingPropertyNamingRule()
}
