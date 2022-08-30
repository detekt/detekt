package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingBetweenFunctionNameAndOpeningParenthesisRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#spacing-between-function-name-and-opening-parenthesis) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class SpacingBetweenFunctionNameAndOpeningParenthesis(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenFunctionNameAndOpeningParenthesisRule()
    override val issue = issueFor("Ensure consistent spacing between function name and opening parenthesis.")
}
