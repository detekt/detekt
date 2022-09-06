package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.UnnecessaryParenthesesBeforeTrailingLambdaRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#unnecessary-parenthesis-before-trailing-lambda)
 * for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class UnnecessaryParenthesesBeforeTrailingLambda(config: Config) : FormattingRule(config) {

    override val wrapping = UnnecessaryParenthesesBeforeTrailingLambdaRule()
    override val issue = issueFor("Ensures there are no unnecessary parentheses before a trailing lambda")
}
