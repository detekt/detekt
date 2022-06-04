package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingBetweenDeclarationsWithCommentsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.10.0")
class SpacingBetweenDeclarationsWithComments(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenDeclarationsWithCommentsRule()
    override val issue = issueFor(
        // message reported by the KtLint rule
        "Declarations and declarations with comments should have an empty space between."
    )
}
