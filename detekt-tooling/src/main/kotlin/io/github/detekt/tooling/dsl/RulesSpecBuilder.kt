package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.RulesSpec
import io.github.detekt.tooling.api.spec.MaxIssuePolicy

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateExperimentalRules: Boolean = false
    var policy: MaxIssuePolicy = MaxIssuePolicy.NoneAllowed()
    var autoCorrect: Boolean = false

    override fun build(): RulesSpec = RulesModel(activateExperimentalRules, policy, autoCorrect)
}

internal data class RulesModel(
    override val activateExperimentalRules: Boolean,
    override val policy: MaxIssuePolicy,
    override val autoCorrect: Boolean,
) : RulesSpec
