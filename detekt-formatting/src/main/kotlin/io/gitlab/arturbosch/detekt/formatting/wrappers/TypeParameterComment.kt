package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.TypeParameterCommentRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
class TypeParameterComment(config: Config) : FormattingRule(config) {

    override val wrapping = TypeParameterCommentRule()
    override val issue = issueFor("Detect discouraged type parameter comment locations.")
}
