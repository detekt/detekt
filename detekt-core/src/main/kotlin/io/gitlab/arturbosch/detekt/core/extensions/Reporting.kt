package io.gitlab.arturbosch.detekt.core.extensions

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.core.DelegatingResult
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

@OptIn(UnstableApi::class)
fun handleReportingExtensions(settings: ProcessingSettings, initialResult: Detektion): Detektion {
    val extensions = loadExtensions<ReportingExtension>(settings)
    extensions.forEach { it.onRawResult(initialResult) }
    val finalResult = extensions.fold(initialResult) { acc, extension ->
        val intermediateValue = extension.transformFindings(acc.findings)
        if (intermediateValue === acc.findings) acc else DelegatingResult(acc, intermediateValue)
    }
    extensions.forEach { it.onFinalResult(finalResult) }
    return finalResult
}
