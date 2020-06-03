package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingBetweenDeclarationsWithAnnotationsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.10.0
 */
class SpacingBetweenDeclarationsWithAnnotations(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenDeclarationsWithAnnotationsRule()
    override val issue = issueFor(
        // message reported by the KtLint rule
        "Declarations and declarations with annotations should have an empty space between."
    )
}
