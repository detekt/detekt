package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoConsecutiveCommentsRule
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-consecutive-comments) for documentation.
 */
class NoConsecutiveComments(config: Config) : KtlintRule(config, "Disallow consecutive comments in most cases.") {

    override val wrapping = NoConsecutiveCommentsRule()
}
