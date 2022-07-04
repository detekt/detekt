package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingBetweenFunctionNameAndOpeningParenthesisRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.21.0")
class SpacingBetweenFunctionNameAndOpeningParenthesis(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenFunctionNameAndOpeningParenthesisRule()
    override val issue = issueFor(
        "Enforce consistent spacing between function name and the opening parenthesis."
    )
}
