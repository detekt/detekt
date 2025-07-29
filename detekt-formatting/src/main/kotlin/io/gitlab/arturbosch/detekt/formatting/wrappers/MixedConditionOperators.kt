package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.MixedConditionOperatorsRule
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/) for documentation.
 */
class MixedConditionOperators(config: Config) : FormattingRule(
    config,
    "Conditions should not use a both '&&' and '||' operators between operators at the same level"
) {

    override val wrapping = MixedConditionOperatorsRule()
}
