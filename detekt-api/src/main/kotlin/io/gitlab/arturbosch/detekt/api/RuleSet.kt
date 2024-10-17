package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: Id, val rules: Map<Rule.Name, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: Id, rules: List<(Config) -> Rule>): RuleSet =
            RuleSet(id, rules.associateBy { it(Config.empty).ruleName })
    }

    data class Id(val value: String) {
        init {
            validateIdentifier(value)
        }

        override fun toString(): String = value
    }
}
