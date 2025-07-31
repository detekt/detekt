package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoLineBreakBeforeAssignmentRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-line-break-before-assignment) for
 * documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoLineBreakBeforeAssignment(config: Config) : FormattingRule(
    config,
    "Reports line breaks before assignment"
) {

    override val wrapping = NoLineBreakBeforeAssignmentRule()
}
