package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.NoConsecutiveCommentsRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-consecutive-comments) for documentation.
 */
internal class NoConsecutiveComments(config: Config) :
    KtlintRule(config, "Disallow consecutive comments in most cases.") {

    override val wrapping = NoConsecutiveCommentsRule()
}
