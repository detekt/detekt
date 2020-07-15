package io.github.detekt.tooling.api.spec

import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId

interface RulesSpec {

    /**
     * Activates all rules which do not have the active property set to 'true' by default.
     *
     * Also known as 'failFast' flag.
     */
    val activateExperimentalRules: Boolean

    /**
     * Sets the policy for allowed max issues found during the analysis.
     */
    val maxIssuePolicy: MaxIssuePolicy

    /**
     * Issues which were corrected should not be taken into account for calculating the max issue threshold.
     */
    val excludeCorrectable: Boolean

    /**
     * Policy on how many issues are allowed before detekt throws an error.
     */
    sealed class MaxIssuePolicy {

        /**
         * Marker that MaxIssuePolicy should be read from the config file when available.
         * Else it defaults to [NoneAllowed].
         */
        object NonSpecified : MaxIssuePolicy()

        /**
         * Always return exit code 0 on found issues.
         */
        object AllowAny : MaxIssuePolicy()

        /**
         * Never return successfully (code 0) on issues in codebase.
         */
        object NoneAllowed : MaxIssuePolicy()

        /**
         * Define a specific amount of issues which are allowed to find before returning non zero exit code.
         */
        class AllowAmount(val amount: Int) : MaxIssuePolicy()
    }

    /**
     * Should detekt create mutable ASTs which [io.gitlab.arturbosch.detekt.api.Rule]s can manipulate?
     */
    val autoCorrect: Boolean

    /**
     * Allows to programmatically restrict the execution of certain rules.
     *
     * By default there are no restrictions which means all loaded rule sets and rules are considered to be executed.
     */
    val runPolicy: RunPolicy

    /**
     * Restrict rules to use for current analysis.
     */
    sealed class RunPolicy {

        /**
         * Run all loaded rules provided by [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
         */
        object NoRestrictions : RunPolicy()

        /**
         * Run a single rule.
         */
        class RestrictToSingleRule(val id: Pair<RuleSetId, RuleId>) : RunPolicy()
    }
}
