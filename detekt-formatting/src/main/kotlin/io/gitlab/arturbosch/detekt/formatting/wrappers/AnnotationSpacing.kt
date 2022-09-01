package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.AnnotationSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#annotation-spacing) for documentation.
 */
@AutoCorrectable(since = "1.0.0")
@ActiveByDefault(since = "1.22.0")
class AnnotationSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = AnnotationSpacingRule()
    override val issue =
        issueFor("There should not be empty lines between an annotation and the object that it's annotating")
}
