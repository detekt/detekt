package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport

internal fun defaultReportMapping(report: OutputReport) =
    if (report is BuiltInOutputReport) report.ending else report.id

internal fun printIssues(issues: Map<String, List<Issue>>): String =
    buildString {
        issues.forEach { (key, issues) ->
            append(key)
            append("\n")
            issues.forEach {
                append("\t")
                append(it.detailed().yellow())
                append("\n")
            }
        }
    }

const val DETEKT_OUTPUT_REPORT_PATHS_KEY = "detekt.output.report.paths.key"

private const val REPORT_MESSAGE_SIZE_LIMIT = 80
private val messageReplacementRegex = Regex("\\s+")

fun Detektion.filterAutoCorrectedIssues(): List<Issue> = issues.filterNot { issue -> issue.autoCorrectEnabled }

private fun Issue.truncatedMessage(): String {
    val message = message
        .replace(messageReplacementRegex, " ")
        .trim()
    return when {
        message.length > REPORT_MESSAGE_SIZE_LIMIT -> "${message.take(REPORT_MESSAGE_SIZE_LIMIT)}(...)"
        else -> message
    }
}

private fun Issue.detailed(): String = "${ruleInstance.id} - [${truncatedMessage()}] at ${location.compact()}"

internal fun Location.compact(): String = "$path:$source"
