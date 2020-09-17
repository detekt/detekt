package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.AnnotationSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.0.0
 */
class AnnotationSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = AnnotationSpacingRule()
    override val issue =
        issueFor("There should not be empty lines between an annotation and the object that it's annotating")
}
