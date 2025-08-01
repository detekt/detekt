package dev.detekt.tooling.api.spec

interface LoggingSpec {

    /**
     * Print debug messages like time measuring and extension loading.
     */
    val debug: Boolean

    /**
     * Channel where detekt should write its normal output to.
     */
    val outputChannel: Appendable

    /**
     * Channel where errors should be logged.
     */
    val errorChannel: Appendable
}
