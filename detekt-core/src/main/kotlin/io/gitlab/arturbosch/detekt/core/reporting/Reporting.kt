package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport

internal fun defaultReportMapping(report: OutputReport) =
    if (report is BuiltInOutputReport) report.ending else report.id

internal fun printFindings(findings: Map<String, List<Finding2>>): String {
    return buildString {
        findings.forEach { (key, issues) ->
            append(key)
            append("\n")
            issues.forEach {
                append("\t")
                append(it.detailed().yellow())
                append("\n")
            }
        }
    }
}

const val BUILD = "build"
const val EXCLUDE_CORRECTABLE = "excludeCorrectable"

const val DETEKT_OUTPUT_REPORT_PATHS_KEY = "detekt.output.report.paths.key"
const val DETEKT_OUTPUT_REPORT_BASE_PATH_KEY = "detekt.output.report.base.path"

private const val REPORT_MESSAGE_SIZE_LIMIT = 80
private val messageReplacementRegex = Regex("\\s+")

fun Config.excludeCorrectable(): Boolean = subConfig(BUILD).valueOrDefault(EXCLUDE_CORRECTABLE, false)

fun Detektion.filterEmptyIssues(config: Config): List<Finding2> {
    return this
        .filterAutoCorrectedIssues(config)
}

fun Detektion.filterAutoCorrectedIssues(config: Config): List<Finding2> {
    if (!config.excludeCorrectable()) {
        return findings
    }
    return findings.filter { finding -> !finding.autoCorrectEnabled }
}

private fun Finding2.truncatedMessage(): String {
    val message = message
        .replace(messageReplacementRegex, " ")
        .trim()
    return when {
        message.length > REPORT_MESSAGE_SIZE_LIMIT -> "${message.take(REPORT_MESSAGE_SIZE_LIMIT)}(...)"
        else -> message
    }
}

private fun Finding2.detailed(): String = "${ruleInfo.id} - [${truncatedMessage()}] at ${location.compact()}"
