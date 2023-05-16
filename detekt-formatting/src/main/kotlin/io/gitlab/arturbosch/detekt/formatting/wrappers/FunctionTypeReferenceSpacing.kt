package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunctionTypeReferenceSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/@KTLINT_VERSION@/rules/experimental/#function-type-reference-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class FunctionTypeReferenceSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionTypeReferenceSpacingRule()
    override val issue =
        issueFor("Checks the spacing before and after the angle brackets of a type argument list.")
}
