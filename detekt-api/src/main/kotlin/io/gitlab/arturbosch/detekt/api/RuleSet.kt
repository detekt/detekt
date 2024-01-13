package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: Id, val rules: Map<String, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: Id, rules: List<(Config) -> Rule>): RuleSet {
            return RuleSet(id, rules.associateBy { it(Config.empty).ruleId })
        }
    }

    class Id(val value: String) {
        init {
            validateIdentifier(value)
        }

        override fun toString(): String {
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            return value == other.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }
}
