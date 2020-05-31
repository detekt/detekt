package io.gitlab.arturbosch.detekt.api

/**
 * Allows to intercept detekt's result container by listening to the initial and final state
 * and manipulate the reported findings.
 */
@UnstableApi
interface ReportingExtension : Extension {

    /**
     * Is called before any [transformFindings] calls were executed.
     */
    fun onRawResult(result: Detektion) {
        // intercept for the initial results
    }

    /**
     * Allows to transform the reported findings e.g. apply custom filtering.
     */
    fun transformFindings(findings: Map<RuleSetId, List<Finding>>): Map<RuleSetId, List<Finding>> = findings

    /**
     * Is called after all extensions's [transformFindings] were called.
     */
    fun onFinalResult(result: Detektion) {
        // intercept for the final results
    }
}
