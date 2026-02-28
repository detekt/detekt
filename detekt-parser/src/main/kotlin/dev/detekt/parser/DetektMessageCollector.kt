package dev.detekt.parser

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.PlainTextMessageRenderer

class DetektMessageCollector(private val minSeverity: CompilerMessageSeverity, private val printer: (String) -> Unit) :
    MessageCollector by MessageCollector.NONE {
    private var messages = 0

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.ordinal <= minSeverity.ordinal) {
            printer(DetektMessageRenderer.render(severity, message, location))
            messages++
        }
    }

    fun printIssuesCountIfAny() {
        if (messages > 0) {
            printer("There were $messages compiler errors found during analysis. This affects accuracy of reporting.")
        }
    }
}

private object DetektMessageRenderer : PlainTextMessageRenderer() {
    override fun getName() = "detekt message renderer"
    override fun getPath(location: CompilerMessageSourceLocation) = location.path
}
