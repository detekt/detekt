package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunctionNamingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/@KTLINT_VERSION@/rules/experimental/#function-naming) for
 * documentation.
 */
class FunctionName(config: Config) : FormattingRule(config) {
    override val wrapping = FunctionNamingRule()
    override val issue =
        issueFor("Function name should start with a lowercase letter (except factory methods) and use camel case.")
}
