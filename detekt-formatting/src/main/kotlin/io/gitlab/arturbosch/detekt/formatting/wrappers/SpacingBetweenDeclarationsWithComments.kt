package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingBetweenDeclarationsWithCommentsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#blank-line-between-declaration-with-comments)
 * for documentation.
 */
@AutoCorrectable(since = "1.10.0")
@ActiveByDefault(since = "1.22.0")
class SpacingBetweenDeclarationsWithComments(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenDeclarationsWithCommentsRule()
    override val issue = issueFor(
        // message reported by the KtLint rule
        "Declarations and declarations with comments should have an empty space between."
    )
}
