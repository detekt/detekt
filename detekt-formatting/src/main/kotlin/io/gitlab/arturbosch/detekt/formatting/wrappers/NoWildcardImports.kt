package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoWildcardImportsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-import">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 */
class NoWildcardImports(config: Config) : FormattingRule(config) {

    override val wrapping = NoWildcardImportsRule()
    override val issue = issueFor("Detects wildcast import usages")
}
