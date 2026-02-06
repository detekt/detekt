package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingBetweenDeclarationsWithCommentsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-between-declaration-with-comments)
 * for documentation.
 */
@AutoCorrectable(since = "1.10.0")
@ActiveByDefault(since = "1.22.0")
class SpacingBetweenDeclarationsWithComments(config: Config) :
    KtlintRule(config, "Declarations and declarations with comments should have an empty space between.") {

    override val wrapping = SpacingBetweenDeclarationsWithCommentsRule()
}
