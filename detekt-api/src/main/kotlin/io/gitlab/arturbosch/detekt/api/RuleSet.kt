package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(@get:JvmName("getId") val id: Id, val rules: Map<Rule.Id, (Config) -> Rule>) {
    companion object {
        @JvmName("invoke")
        operator fun invoke(id: Id, rules: List<(Config) -> Rule>): RuleSet {
            return RuleSet(id, rules.associateBy { it(Config.empty).ruleId })
        }
    }

    @JvmInline
    value class Id(val value: String) {
        init {
            validateIdentifier(value)
        }

        override fun toString(): String {
            return value
        }
    }
}
