package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.FunctionTypeReferenceSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class FunctionTypeReferenceSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionTypeReferenceSpacingRule()
    override val issue =
        issueFor("Checks the spacing before and after the angle brackets of a type argument list.")
}
