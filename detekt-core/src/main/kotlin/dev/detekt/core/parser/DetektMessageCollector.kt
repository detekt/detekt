package dev.detekt.core.parser

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class DetektMessageCollector(
    private val minSeverity: CompilerMessageSeverity,
    private val debugPrinter: (() -> String) -> Unit,
    private val warningPrinter: (String) -> Unit,
    private val isDebugEnabled: Boolean,
) : MessageCollector by MessageCollector.NONE {
    private var messages = 0

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.ordinal <= minSeverity.ordinal) {
            debugPrinter { renderCompilerMessage(severity, message, location) }
            messages++
        }
    }

    fun printIssuesCountIfAny() {
        if (messages > 0) {
            val header =
                "There were $messages compiler errors found during analysis. This affects accuracy of reporting."
            val suggestion = if (!isDebugEnabled) {
                "\nRun detekt CLI with --debug or set `detekt { debug = true }` in Gradle to see the error messages."
            } else {
                ""
            }
            warningPrinter(header + suggestion)
        }
    }
}

internal fun renderCompilerMessage(
    severity: CompilerMessageSeverity,
    message: String,
    location: CompilerMessageSourceLocation?,
): String =
    buildString {
        if (location != null) {
            append(location.path)
            append(':')
            append(location.line)
            append(':')
            append(location.column)
            append(' ')
        }
        append(severity.presentableName)
        append(": ")
        append(message)
    }
