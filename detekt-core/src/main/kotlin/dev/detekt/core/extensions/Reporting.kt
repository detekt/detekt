package dev.detekt.core.extensions

import dev.detekt.api.Detektion
import dev.detekt.api.ReportingExtension
import dev.detekt.core.DelegatingResult
import dev.detekt.core.ProcessingSettings

fun handleReportingExtensions(settings: ProcessingSettings, initialResult: Detektion): Detektion {
    val extensions = loadExtensions<ReportingExtension>(settings)
    extensions.forEach { it.onRawResult(initialResult) }
    val finalResult = extensions.fold(initialResult) { acc, extension ->
        val intermediateValue = extension.transformIssues(acc.issues)
        if (intermediateValue === acc.issues) acc else DelegatingResult(acc, intermediateValue)
    }
    extensions.forEach { it.onFinalResult(finalResult) }
    return finalResult
}
