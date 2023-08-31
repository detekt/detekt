package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.RulesSpec
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy
import io.gitlab.arturbosch.detekt.api.Severity

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateAllRules: Boolean = false
    var failurePolicy: FailurePolicy = FailurePolicy.FailOnSeverity(Severity.ERROR)
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
    override val failurePolicy: FailurePolicy,
    override val excludeCorrectable: Boolean,
    override val autoCorrect: Boolean,
    override val runPolicy: RulesSpec.RunPolicy
) : RulesSpec
