package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.BlockCommentInitialStarAlignmentRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 */
@AutoCorrectable(since = "1.20.0")
class BlockCommentInitialStarAlignment(config: Config) : FormattingRule(config) {

    override val wrapping = BlockCommentInitialStarAlignmentRule()
    override val issue = issueFor("Detect the alignment of the initial star in a block comment.")
}
