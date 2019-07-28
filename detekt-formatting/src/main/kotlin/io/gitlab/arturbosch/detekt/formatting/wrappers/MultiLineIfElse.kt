package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.MultiLineIfElseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-modifier-order">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class MultiLineIfElse(config: Config) : FormattingRule(config) {

    override val wrapping = MultiLineIfElseRule()
    override val issue = issueFor("Detects multiline if-else statements without braces")
}
