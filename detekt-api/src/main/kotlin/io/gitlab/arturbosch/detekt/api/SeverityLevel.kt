package io.gitlab.arturbosch.detekt.api

/**
 * The severity level for each [Issue]. This will be printed to the output, such as XML or Sarif.
 *
 * In the future, this will be used to determine the process result and reports.
 */
enum class SeverityLevel {

    /**
     * Fail the cli process or gradle task.
     */
    ERROR,

    /**
     * Report issue and contribute to the total count, but does not fail cli process or gradle task.
     */
    WARNING,

    /**
     * Report issue, does not contribute to the count.
     */
    INFO
}
