package dev.detekt.tooling.api.spec

import dev.detekt.api.RuleSetId
import dev.detekt.api.Severity

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
     * Policy to decide if detekt throws an error.
     */
    sealed interface FailurePolicy {

        /**
         * Any number of issues is allowed. The build never fails due to detekt issues.
         */
        data object NeverFail : FailurePolicy

        /**
         * No issue at or above the specified severity is allowed.
         */
        data class FailOnSeverity(val minSeverity: Severity) : FailurePolicy
    }

    /**
     * Should detekt create mutable ASTs which [dev.detekt.api.Rule]s can manipulate?
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
    sealed interface RunPolicy {

        /**
         * Run all loaded rules provided by [dev.detekt.api.RuleSetProvider]
         */
        data object NoRestrictions : RunPolicy

        /**
         * Exclude all default rule sets provided by detekt.
         */
        data object DisableDefaultRuleSets : RunPolicy

        /**
         * Run a single rule.
         */
        class RestrictToSingleRule(val ruleSetId: RuleSetId, val ruleId: String) : RunPolicy
    }
}
