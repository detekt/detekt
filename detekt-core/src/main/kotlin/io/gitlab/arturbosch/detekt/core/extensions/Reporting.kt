package io.gitlab.arturbosch.detekt.core.extensions

import dev.detekt.api.Detektion
import dev.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.core.DelegatingResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

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
