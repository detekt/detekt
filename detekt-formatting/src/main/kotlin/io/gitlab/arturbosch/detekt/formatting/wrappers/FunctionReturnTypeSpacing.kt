package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.FunctionReturnTypeSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#function-return-type-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class FunctionReturnTypeSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionReturnTypeSpacingRule()
    override val issue = issueFor("Checks the spacing between colon and return type.")
}
