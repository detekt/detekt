package io.gitlab.arturbosch.detekt.api

/**
 * The severity for each [Issue]. This will be printed to the output, such as XML or Sarif.
 * Depending on the severity of the issues found, the build process result is determined.
 */
enum class Severity {
    Error,
    Warning,
    Info;

    internal companion object {
        val DEFAULT = Error

        fun fromString(severity: String): Severity {
            val lowercase = severity.lowercase()
            return entries.find { it.name.lowercase() == lowercase }
                ?: error("$severity is not a valid Severity. Allowed values are ${Severity.entries}")
        }
    }
}
