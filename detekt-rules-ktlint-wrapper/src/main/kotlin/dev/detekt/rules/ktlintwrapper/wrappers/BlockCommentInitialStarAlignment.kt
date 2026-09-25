package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlockCommentInitialStarAlignmentRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#block-comment-initial-star-alignment) for
 * documentation.
 */
@ActiveByDefault(since = "1.23.0")
internal class BlockCommentInitialStarAlignment(config: Config) :
    KtlintRule(config, "Detect the alignment of the initial star in a block comment."),
    AutoCorrectable {

    override val wrapping = BlockCommentInitialStarAlignmentRule()
}
