package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.WrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.
 */
@ActiveByDefault(since = "1.20.0")
@AutoCorrectable(since = "1.20.0")
class Wrapping(config: Config) : FormattingRule(config) {

    override val wrapping = WrappingRule()
    override val issue = issueFor("Reports missing newlines (e.g. between parentheses of a multi-line function call")
}
