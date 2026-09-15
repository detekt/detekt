package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FunctionTypeReferenceSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#function-type-reference-spacing) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
internal class FunctionTypeReferenceSpacing(config: Config) :
    KtlintRule(config, "Checks the spacing before and after the angle brackets of a type argument list."),
    AutoCorrectable {

    override val wrapping = FunctionTypeReferenceSpacingRule()
}
