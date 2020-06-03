package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingBetweenDeclarationsWithCommentsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.10.0
 */
class SpacingBetweenDeclarationsWithComments(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenDeclarationsWithCommentsRule()
    override val issue = issueFor(
        // message reported by the KtLint rule
        "Declarations and declarations with comments should have an empty space between."
    )
}
