package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.AnnotationSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#annotation-spacing) for documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class AnnotationSpacing(config: Config) :
    KtlintRule(config, "There should not be empty lines between an annotation and the object that it's annotating"),
    AutoCorrectable {

    override val wrapping = AnnotationSpacingRule()
}
