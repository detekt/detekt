package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId

data class CollectedRules(
    val ruleSets: List<RuleSet>,
) {
    data class RuleSet(
        val id: RuleSetId,
        val active: Boolean,
        val rules: List<Rule>,
    )

    data class Rule(
        val id: RuleId,
        val active: Boolean,
    )
}
