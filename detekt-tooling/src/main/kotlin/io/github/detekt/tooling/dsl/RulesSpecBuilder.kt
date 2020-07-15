package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.RulesSpec

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateExperimentalRules: Boolean = false
    var maxIssuePolicy: RulesSpec.MaxIssuePolicy = RulesSpec.MaxIssuePolicy.NoneAllowed
    var excludeCorrectable: Boolean = false
    var autoCorrect: Boolean = false
    var runPolicy: RulesSpec.RunPolicy = RulesSpec.RunPolicy.NoRestrictions

    override fun build(): RulesSpec = RulesModel(
        activateExperimentalRules,
        maxIssuePolicy,
        excludeCorrectable,
        autoCorrect,
        runPolicy
    )
}

internal data class RulesModel(
    override val activateExperimentalRules: Boolean,
    override val maxIssuePolicy: RulesSpec.MaxIssuePolicy,
    override val excludeCorrectable: Boolean,
    override val autoCorrect: Boolean,
    override val runPolicy: RulesSpec.RunPolicy
) : RulesSpec
