package io.gitlab.arturbosch.detekt.api.v2

/**
 * Allows to intercept detekt's result container by listening to the initial and final state
 * and manipulate the reported findings.
 */
interface ReportingModifier {

    val priority: Int
        get() = 0

    /**
     * Is called before any [transform] calls were executed.
     */
    fun onRawResult(result: Detektion) {
        // intercept for the initial results
    }

    /**
     * Allows to transform the reported findings e.g. apply custom filtering.
     */
    fun transform(detektion: Detektion): Detektion = detektion

    /**
     * Is called after all extensions's [transform] were called.
     */
    fun onFinalResult(result: Detektion) {
        // intercept for the final results
    }
}

