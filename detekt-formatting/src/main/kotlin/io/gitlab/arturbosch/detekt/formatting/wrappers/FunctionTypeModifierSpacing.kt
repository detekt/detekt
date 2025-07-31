package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunctionTypeModifierSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-type-modifier-spacing)
 * for documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "2.0.0")
class FunctionTypeModifierSpacing(config: Config) : FormattingRule(
    config,
    "Enforce a single whitespace between the modifier list and the function type."
) {

    override val wrapping = FunctionTypeModifierSpacingRule()
}
