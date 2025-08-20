package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunctionTypeReferenceSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-type-reference-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class FunctionTypeReferenceSpacing(config: Config) : FormattingRule(
    config,
    "Checks the spacing before and after the angle brackets of a type argument list."
) {

    override val wrapping = FunctionTypeReferenceSpacingRule()
}
