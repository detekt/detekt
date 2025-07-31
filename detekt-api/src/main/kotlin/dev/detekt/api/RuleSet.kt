package dev.detekt.api

import dev.detekt.api.internal.validateIdentifier
import dev.drewhamilton.poko.Poko

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: Id, val rules: Map<RuleName, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: Id, rules: List<(Config) -> Rule>): RuleSet =
            RuleSet(id, rules.associateBy { it(Config.empty).ruleName })
    }

    @Poko
    class Id(val value: String) {
        init {
            validateIdentifier(value)
        }

        override fun toString(): String = value
    }
}
