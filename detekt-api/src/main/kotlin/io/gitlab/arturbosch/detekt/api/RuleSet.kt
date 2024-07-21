package io.gitlab.arturbosch.detekt.api

import dev.drewhamilton.poko.Poko

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 */
class RuleSet(val id: Id, val rules: Map<Rule.Name, (Config) -> Rule>) {
    companion object {
        operator fun invoke(id: Id, rules: List<(Config) -> Rule>): RuleSet =
            RuleSet(id, rules.associateBy { it(Config.empty).ruleName })
    }

    @Poko
    class Id(val value: String) {
        init {
            require(value.matches(idRegex)) { "Id '$value' must match ${idRegex.pattern}" }
        }

        override fun toString(): String = value
    }
}

private val idRegex = Regex("[aA-zZ]+(?:[aA-zZ0-9-]+)*[aA-zZ0-9]")
