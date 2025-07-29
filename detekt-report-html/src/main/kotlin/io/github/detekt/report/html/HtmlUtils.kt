package io.github.detekt.report.html

import dev.detekt.api.SourceLocation
import dev.detekt.api.internal.whichDetekt
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.h4
import kotlinx.html.p
import kotlinx.html.pre
import kotlinx.html.span
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

internal fun FlowContent.snippetCode(
    id: String,
    lines: Sequence<String>,
    location: SourceLocation,
    length: Int,
) {
    try {
        pre {
            code {
                val dropLineCount = max(location.line - 1 - EXTRA_LINES_IN_SNIPPET, 0)
                val takeLineCount = EXTRA_LINES_IN_SNIPPET + 1 + min(location.line - 1, EXTRA_LINES_IN_SNIPPET)
                var currentLineNumber = dropLineCount + 1
                var errorLength = length
                lines
                    .drop(dropLineCount)
                    .take(takeLineCount)
                    .forEach { line ->
                        span("lineno") { text("%1$4s ".format(Locale.ROOT, currentLineNumber)) }
                        if (currentLineNumber >= location.line && errorLength > 0) {
                            val column = if (currentLineNumber == location.line) location.column - 1 else 0
                            errorLength -= writeErrorLine(line, column, errorLength) + 1 // we need to consume the \n
                        } else {
                            text(line)
                        }
                        text("\n")
                        currentLineNumber++
                    }
            }
        }
    } catch (@Suppress("TooGenericExceptionCaught") ex: Throwable) {
        showError(id, ex)
    }
}

private fun FlowContent.writeErrorLine(line: String, errorStarts: Int, length: Int): Int {
    val errorEnds = min(errorStarts + length, line.length)
    text(line.substring(startIndex = 0, endIndex = errorStarts))
    span("error") {
        text(
            line.substring(
                startIndex = errorStarts,
                endIndex = errorEnds
            )
        )
    }
    text(line.substring(startIndex = errorEnds))
    return errorEnds - errorStarts
}

private fun FlowContent.showError(id: String, throwable: Throwable) {
    div("exception") {
        h4 {
            text("Error showing the code snippet")
        }

        p {
            text("This seems to be an error in the rule $id, please ")
            a(createReportUrl(id, throwable)) {
                text("report this issue")
            }
            text(".")
        }
    }
}

private fun createReportUrl(ruleId: String, throwable: Throwable): String {
    val title = URLEncoder.encode("HtmlReport error in rule: $ruleId", "UTF8")
    val stackTrace = throwable.printStackTraceString()
        .lineSequence()
        .take(STACK_TRACE_LINES_TO_SHOW)
        .joinToString("\n")
    val bodyMessage = """
        |I found an error in the html report:
        |- Rule: $ruleId
        |- detekt version: ${whichDetekt()}"}
        |- Stacktrace:
        |```
        |$stackTrace
        |```
        |- How to reproduce it: <WRITE HERE HOW TO REPRODUCE THIS ISSUE. A CODE SNIPPET IS THE BEST WAY.>
        |
    """.trimMargin()
    val body = URLEncoder.encode(bodyMessage, "UTF8")

    return "https://github.com/detekt/detekt/issues/new?body=$body&title=$title"
}

private fun Throwable.printStackTraceString(): String {
    val stringWriter = StringWriter()
    printStackTrace(PrintWriter(stringWriter))
    return stringWriter.toString()
}

private const val EXTRA_LINES_IN_SNIPPET = 3
private const val STACK_TRACE_LINES_TO_SHOW = 6
