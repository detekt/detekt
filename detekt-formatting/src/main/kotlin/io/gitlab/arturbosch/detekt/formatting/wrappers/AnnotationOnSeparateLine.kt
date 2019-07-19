package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.AnnotationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 */
class AnnotationOnSeparateLine(config: Config) : FormattingRule(config) {

    override val wrapping = AnnotationRule()
    override val issue = issueFor("Multiple annotations should be placed on separate lines. ")
}
