package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.TypeArgumentCommentRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
class TypeArgumentComment(config: Config) : FormattingRule(
    config,
    "Detect discouraged type argument comment locations."
) {

    override val wrapping = TypeArgumentCommentRule()
}
