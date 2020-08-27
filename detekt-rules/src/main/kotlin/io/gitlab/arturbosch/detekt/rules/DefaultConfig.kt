package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.dsl.RuleBuilder
import io.gitlab.arturbosch.detekt.api.dsl.rules
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.rules.coroutines.GlobalCoroutineUsage
import io.gitlab.arturbosch.detekt.rules.coroutines.RedundantSuspendModifier

val defaultConfig: List<RuleBuilder<BaseRule>> = rules {
    ruleSet("Coroutines") {
        active = true
        GlobalCoroutineUsage {
            active = false
        }
        RedundantSuspendModifier {
            active = false
        }
    }
}
