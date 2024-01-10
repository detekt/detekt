package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

typealias RuleSetId = String

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: RuleSetId, val rules: Map<String, (Config) -> Rule>) {

    init {
        validateIdentifier(id)
    }

    companion object {
        operator fun invoke(id: RuleSetId, rules: List<(Config) -> Rule>): RuleSet {
            return RuleSet(id, rules.associateBy { it(Config.empty).ruleId })
        }
    }
}
