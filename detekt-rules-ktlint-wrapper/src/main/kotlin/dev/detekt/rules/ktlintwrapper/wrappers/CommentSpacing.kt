package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.CommentSpacingRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#comment-spacing) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class CommentSpacing(config: Config) :
    KtlintRule(config, "Checks if comments have the right spacing"),
    AutoCorrectable {

    override val wrapping = CommentSpacingRule()
}
