package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ModifierOrderRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#modifier-order) for documentation.
 *
 * This rules overlaps with [style>ModifierOrder](https://detekt.dev/style.html#modifierorder)
 * from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class ModifierOrdering(config: Config) : FormattingRule(
    config,
    "Detects modifiers in non default order"
) {

    override val wrapping = ModifierOrderRule()
}
