package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ParameterListSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#parameter-list-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class ParameterListSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = ParameterListSpacingRule()
    override val issue = issueFor("Ensure consistent spacing inside the parameter list.")
}
