package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.SourceLocation
import kotlinx.html.FlowContent
import kotlinx.html.code
import kotlinx.html.pre
import kotlinx.html.span
import kotlin.math.max
import kotlin.math.min

internal fun FlowContent.snippetCode(lines: Sequence<String>, location: SourceLocation, length: Int) {
    pre {
        code {
            val dropLineCount = max(location.line - 1 - EXTRA_LINES_IN_SNIPPET, 0)
            val takeLineCount = EXTRA_LINES_IN_SNIPPET + 1 + Math.min(location.line - 1, EXTRA_LINES_IN_SNIPPET)
            var currentLineNumber = dropLineCount + 1
            var errorLength = length
            lines
                .drop(dropLineCount)
                .take(takeLineCount)
                .forEach { line ->
                    span("lineno") { text("%1$4s ".format(currentLineNumber)) }
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

private const val EXTRA_LINES_IN_SNIPPET = 3
