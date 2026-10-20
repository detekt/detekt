package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.SpacingBetweenDeclarationsWithAnnotationsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-between-declarations-with-annotations)
 * for documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class SpacingBetweenDeclarationsWithAnnotations(config: Config) :
    KtlintRule(config, "Declarations and declarations with annotations should have an empty space between."),
    AutoCorrectable {

    override val wrapping = SpacingBetweenDeclarationsWithAnnotationsRule()
}
