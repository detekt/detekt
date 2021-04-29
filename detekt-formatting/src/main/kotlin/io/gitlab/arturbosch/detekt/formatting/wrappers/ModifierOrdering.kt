package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.ModifierOrderRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-modifier-order">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.0.0
 */
@ActiveByDefault(since = "1.0.0")
class ModifierOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ModifierOrderRule()
    override val issue = issueFor("Detects modifiers in non default order")
}
