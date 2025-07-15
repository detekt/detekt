package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Severity

enum class FailureSeverity {
    Error,
    Warning,
    Info,
    Never,
    ;

    internal fun toSeverity(): Severity =
        when (this) {
            Error -> Severity.Error
            Warning -> Severity.Warning
            Info -> Severity.Info
            Never -> error("'$this' does not have a corresponding severity.")
        }

    internal companion object {
        fun fromString(value: String): FailureSeverity {
            val lowercase = value.lowercase()
            return FailureSeverity.entries.find { it.name.lowercase() == lowercase }
                ?: throw IllegalArgumentException(
                    "'$value' is not a valid FailureSeverity. " +
                        "Allowed values are ${FailureSeverity.entries}"
                )
        }
    }
}
