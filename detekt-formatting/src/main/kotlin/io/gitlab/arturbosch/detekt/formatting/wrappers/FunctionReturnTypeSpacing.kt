package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.FunctionReturnTypeSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.21.0")
class FunctionReturnTypeSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionReturnTypeSpacingRule()
    override val issue = issueFor(
        "Ensures consistent spacing around the function return type."
    )
}
