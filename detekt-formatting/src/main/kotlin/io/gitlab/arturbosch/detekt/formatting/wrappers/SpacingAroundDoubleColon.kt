package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingAroundDoubleColonRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@ActiveByDefault(since = "1.21.0")
class SpacingAroundDoubleColon(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundDoubleColonRule()
    override val issue = issueFor("Reports spaces around double colons")
}
