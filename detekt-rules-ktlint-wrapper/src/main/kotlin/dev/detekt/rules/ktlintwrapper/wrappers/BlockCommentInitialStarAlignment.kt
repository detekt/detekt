package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlockCommentInitialStarAlignmentRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#block-comment-initial-star-alignment) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class BlockCommentInitialStarAlignment(config: Config) :
    KtlintRule(config, "Detect the alignment of the initial star in a block comment.") {

    override val wrapping = BlockCommentInitialStarAlignmentRule()
}
