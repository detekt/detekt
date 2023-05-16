package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlockCommentInitialStarAlignmentRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/@KTLINT_VERSION@/rules/experimental/#block-comment-initial-star-alignment) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class BlockCommentInitialStarAlignment(config: Config) : FormattingRule(config) {

    override val wrapping = BlockCommentInitialStarAlignmentRule()
    override val issue = issueFor("Detect the alignment of the initial star in a block comment.")
}
