package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ModifierListSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#modifier-list-spacing) for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class ModifierListSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = ModifierListSpacingRule()
    override val issue =
        issueFor("Checks the spacing between the modifiers in and after the last modifier in a modifier list.")
}
