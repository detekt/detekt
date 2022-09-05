package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.NullableTypeSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#nullable-type-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class NullableTypeSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = NullableTypeSpacingRule()
    override val issue = issueFor("Ensure no spaces in nullable type.")
}
