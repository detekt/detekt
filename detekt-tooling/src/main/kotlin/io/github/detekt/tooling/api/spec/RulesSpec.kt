package io.github.detekt.tooling.api.spec

import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId

interface RulesSpec {

    /**
     * Activates all rules which do not have the active property set to 'true' by default.
     */
    val activateAllRules: Boolean

    /**
     * Sets the policy to handle issues found during the analysis.
     */
    val failurePolicy: FailurePolicy

    /**
     * Issues which were corrected should not be taken into account for calculating the max issue threshold.
     */
    val excludeCorrectable: Boolean

    /**
     * Policy to decide if detekt throws an error.
     */
    sealed class FailurePolicy {

        /**
         * No issues with a severity of error is allowed.
         */
        data object FailOnError : FailurePolicy()
    }

    /**
     * Should detekt create mutable ASTs which [io.gitlab.arturbosch.detekt.api.Rule]s can manipulate?
     */
    val autoCorrect: Boolean

    /**
     * Allows to programmatically restrict the execution of certain rules.
     *
     * By default, there are no restrictions which means all loaded rule sets and rules are considered to be executed.
     */
    val runPolicy: RunPolicy

    /**
     * Restrict rules to use for current analysis.
     */
    sealed class RunPolicy {

        /**
         * Run all loaded rules provided by [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
         */
        data object NoRestrictions : RunPolicy()

        /**
         * Run a single rule.
         */
        class RestrictToSingleRule(val id: Pair<RuleSetId, RuleId>) : RunPolicy()
    }
}
