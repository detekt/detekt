package dev.detekt.core.extensions

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.ReportingExtension
import dev.detekt.core.ProcessingSettings

fun handleReportingExtensions(settings: ProcessingSettings, initialResult: Detektion): Detektion {
    val extensions = loadExtensions<ReportingExtension>(settings)
    extensions.forEach { it.onRawResult(initialResult) }
    val finalIssues = extensions.fold(initialResult.issues) { acc, extension -> extension.transformIssues(acc) }
    val finalResult = initialResult.copy(issues = finalIssues)
    extensions.forEach { it.onFinalResult(finalResult) }
    return finalResult
}

private fun Detektion.copy(issues: List<Issue> = this.issues): Detektion = Detektion(
    issues = issues,
    rules = rules,
    metrics = metrics,
    notifications = notifications,
    userData = userData,
)
