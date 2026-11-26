package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.ValueParameterCommentRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
internal class ValueParameterComment(config: Config) :
    KtlintRule(config, "Detect discouraged value parameter comment locations.") {

    override val wrapping = ValueParameterCommentRule()
}
