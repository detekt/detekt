package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingBetweenFunctionNameAndOpeningParenthesisRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#spacing-between-function-name-and-opening-parenthesis) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.22.0")
class SpacingBetweenFunctionNameAndOpeningParenthesis(config: Config) :
    KtlintRule(config, "Ensure consistent spacing between function name and opening parenthesis.") {

    override val wrapping = SpacingBetweenFunctionNameAndOpeningParenthesisRule()
}
