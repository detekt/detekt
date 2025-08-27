package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ValueArgumentCommentRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
class ValueArgumentComment(config: Config) : KtlintRule(
    config,
    "Detect discouraged value argument comment locations."
) {

    override val wrapping = ValueArgumentCommentRule()
}
