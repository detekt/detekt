package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.RulesSpec

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateAllRules: Boolean = false
    var maxIssuePolicy: RulesSpec.MaxIssuePolicy = RulesSpec.MaxIssuePolicy.NoneAllowed
    var excludeCorrectable: Boolean = false
    var autoCorrect: Boolean = false
    var runPolicy: RulesSpec.RunPolicy = RulesSpec.RunPolicy.NoRestrictions

    override fun build(): RulesSpec = RulesModel(
        activateAllRules,
        maxIssuePolicy,
        excludeCorrectable,
        autoCorrect,
        runPolicy
    )
}

private data class RulesModel(
    override val activateAllRules: Boolean,
    override val maxIssuePolicy: RulesSpec.MaxIssuePolicy,
    override val excludeCorrectable: Boolean,
    override val autoCorrect: Boolean,
    override val runPolicy: RulesSpec.RunPolicy
) : RulesSpec
