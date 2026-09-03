package dev.detekt.api

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: RuleSetId, val rules: Map<RuleName, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: RuleSetId, rules: List<(Config) -> Rule>): RuleSet =
            RuleSet(id, rules.associateBy { it(Config.empty).ruleName })
    }
}
