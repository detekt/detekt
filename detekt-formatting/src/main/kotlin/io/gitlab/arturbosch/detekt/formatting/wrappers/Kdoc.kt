package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.KdocRule
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#kdoc) for documentation.
 */
class Kdoc(config: Config) : FormattingRule(
    config,
    "Only allow KDoc when comments are in a location that can be converted to public documentation"
) {

    override val wrapping = KdocRule()
}
