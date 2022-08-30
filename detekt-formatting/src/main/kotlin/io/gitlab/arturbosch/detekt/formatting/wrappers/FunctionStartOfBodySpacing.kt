package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.FunctionStartOfBodySpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#function-start-of-body-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class FunctionStartOfBodySpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionStartOfBodySpacingRule()
    override val issue = issueFor("Check for consistent spacing before start of function body.")
}
