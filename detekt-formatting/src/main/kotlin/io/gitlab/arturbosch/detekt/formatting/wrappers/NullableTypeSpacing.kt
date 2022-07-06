package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.NullableTypeSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.21.0")
class NullableTypeSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = NullableTypeSpacingRule()
    override val issue = issueFor(
        "Ensures no spaces in a nullable type."
    )
}
