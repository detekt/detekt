package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.RulesSpec

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateAllRules: Boolean = false
    var failurePolicy: RulesSpec.FailurePolicy = RulesSpec.FailurePolicy.DefaultFailurePolicy
    var excludeCorrectable: Boolean = false
    var autoCorrect: Boolean = false
    var runPolicy: RulesSpec.RunPolicy = RulesSpec.RunPolicy.NoRestrictions

    override fun build(): RulesSpec = RulesModel(
        activateAllRules,
        failurePolicy,
        excludeCorrectable,
        autoCorrect,
        runPolicy
    )
}

private data class RulesModel(
    override val activateAllRules: Boolean,
    override val failurePolicy: RulesSpec.FailurePolicy,
    override val excludeCorrectable: Boolean,
    override val autoCorrect: Boolean,
    override val runPolicy: RulesSpec.RunPolicy
) : RulesSpec
