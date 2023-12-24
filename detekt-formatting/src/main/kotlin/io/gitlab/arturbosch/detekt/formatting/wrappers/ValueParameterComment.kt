package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ValueParameterCommentRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
class ValueParameterComment(config: Config) : FormattingRule(config) {

    override val wrapping = ValueParameterCommentRule()
    override val issue = issueFor("Detect discouraged value parameter comment locations.")
}
