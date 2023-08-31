package io.gitlab.arturbosch.detekt.api

/**
 * The severity for each [Issue]. This will be printed to the output, such as XML or Sarif.
 * Depending on the severity of the issues found, the build process result is determined.
 */
enum class Severity {
    ERROR,
    WARNING,
    INFO;

    internal companion object {
        val DEFAULT = ERROR
    }
}
