package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingBetweenDeclarationsWithAnnotationsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-between-declarations-with-annotations)
 * for documentation.
 */
@AutoCorrectable(since = "1.10.0")
@ActiveByDefault(since = "1.22.0")
class SpacingBetweenDeclarationsWithAnnotations(config: Config) : FormattingRule(
    config,
    "Declarations and declarations with annotations should have an empty space between."
) {

    override val wrapping = SpacingBetweenDeclarationsWithAnnotationsRule()
}
