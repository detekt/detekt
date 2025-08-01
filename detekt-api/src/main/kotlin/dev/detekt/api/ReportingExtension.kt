package dev.detekt.api

/**
 * Allows to intercept detekt's result container by listening to the initial and final state
 * and manipulate the reported issues.
 */
interface ReportingExtension : Extension {

    /**
     * Is called before any [transformIssues] calls were executed.
     */
    fun onRawResult(result: Detektion) {
        // intercept for the initial results
    }

    /**
     * Allows to transform the reported issues e.g. apply custom filtering.
     */
    fun transformIssues(issues: List<Issue>): List<Issue> = issues

    /**
     * Is called after all extensions's [transformIssues] were called.
     */
    fun onFinalResult(result: Detektion) {
        // intercept for the final results
    }
}
