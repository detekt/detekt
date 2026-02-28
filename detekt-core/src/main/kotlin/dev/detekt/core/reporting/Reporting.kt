package dev.detekt.core.reporting

import dev.detekt.api.Issue
import dev.detekt.api.Severity
import java.nio.file.Path

internal fun printIssues(issues: Map<String, List<Issue>>, basePath: Path): String =
    buildString {
        issues.forEach { (key, nestedIssues) ->
            append(key)
            append("\n")
            nestedIssues.forEach {
                append("\t")
                append(it.detailed(basePath).yellow())
                append("\n")
            }
        }
    }

private const val REPORT_MESSAGE_SIZE_LIMIT = 80
private val messageReplacementRegex = Regex("\\s+")

private fun Issue.truncatedMessage(): String {
    val message = message
        .replace(messageReplacementRegex, " ")
        .trim()
    return when {
        message.length > REPORT_MESSAGE_SIZE_LIMIT -> "${message.take(REPORT_MESSAGE_SIZE_LIMIT)}(...)"
        else -> message
    }
}

private fun Issue.detailed(basePath: Path): String =
    "${severity.prefix()}${ruleInstance.id} - [${truncatedMessage()}] at ${location.compact(basePath)}"

internal fun Issue.Location.compact(basePath: Path): String = "${basePath.resolve(path)}:$source"

internal fun Severity.prefix() =
    when (this) {
        Severity.Error -> "e: "
        Severity.Warning -> "w: "
        Severity.Info -> "i: "
    }
