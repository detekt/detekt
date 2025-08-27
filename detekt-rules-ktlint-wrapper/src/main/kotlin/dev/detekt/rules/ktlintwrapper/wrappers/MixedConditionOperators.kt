package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.MixedConditionOperatorsRule
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/) for documentation.
 */
class MixedConditionOperators(config: Config) : KtlintRule(
    config,
    "Conditions should not use a both '&&' and '||' operators between operators at the same level"
) {

    override val wrapping = MixedConditionOperatorsRule()
}
