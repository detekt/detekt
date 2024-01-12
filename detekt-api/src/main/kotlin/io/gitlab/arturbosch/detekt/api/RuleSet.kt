package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: Id, val rules: Map<String, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: String, rules: List<(Config) -> Rule>): RuleSet {
            return RuleSet(Id(id), rules.associateBy { it(Config.empty).ruleId })
        }
    }

    @JvmInline
    value class Id(val value: String) : Comparable<Id> { // FIXME we shouldn't implement Comparable here
        init {
            validateIdentifier(value)
        }

        override fun compareTo(other: Id): Int {
            return value.compareTo(other.value)
        }

        override fun toString(): String {
            return value
        }
    }
}
