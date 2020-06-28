package io.github.detekt.tooling.api.spec

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
    val policy: MaxIssuePolicy

    /**
     * Should detekt create mutable ASTs which [io.gitlab.arturbosch.detekt.api.Rule]s can manipulate?
     */
    val autoCorrect: Boolean
}
