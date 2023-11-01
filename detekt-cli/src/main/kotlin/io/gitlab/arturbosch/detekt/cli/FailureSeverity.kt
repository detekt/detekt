package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Severity

enum class FailureSeverity {
    ERROR,
    WARNING,
    INFO,
    NEVER;

    internal fun toSeverity(): Severity {
        return when (this) {
            ERROR -> Severity.ERROR
            WARNING -> Severity.WARNING
            INFO -> Severity.INFO
            NEVER -> error("'$this' does not have a corresponding severity.")
        }
    }
}
