package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.TypeArgumentListSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class TypeArgumentListSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = TypeArgumentListSpacingRule()
    override val issue = issueFor("Reports spaces in the type reference before a function.")
}
