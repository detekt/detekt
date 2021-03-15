package io.gitlab.arturbosch.detekt.report

import io.github.detekt.sarif4k.SarifSerializer
import java.io.File

/**
 * A naive implementation to merge SARIF assuming all inputs are written by detekt.
 */
object SarifReportMerger {

    fun merge(inputs: Collection<File>, output: File) {
        val sarifs = inputs.filter { it.exists() }.map {
            SarifSerializer.fromJson(it.readText())
        }
        val mergedResults = sarifs.flatMap { it.runs.single().results.orEmpty() }
        val mergedSarif = sarifs[0].copy(runs = listOf(sarifs[0].runs.single().copy(results = mergedResults)))
        output.writeText(SarifSerializer.toJson(mergedSarif))
    }
}
