package io.gitlab.arturbosch.detekt.report

import io.github.detekt.sarif4j.SarifSchema210
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File

/**
 * A naive implementation to merge SARIF assuming all inputs are written by detekt.
 */
object SarifReportMerger {

    fun merge(inputs: Collection<File>, output: File) {
        val objectMapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        val mergedSarif = SarifSchema210()
        inputs.filter { it.exists() }.forEachIndexed { index, input ->
            val sarif = objectMapper.readValue(input, SarifSchema210::class.java)
            if (index == 0) {
                mergedSarif.`$schema` = sarif.`$schema`
                mergedSarif.version = sarif.version
                mergedSarif.runs = sarif.runs
            } else {
                mergedSarif.runs.first().results.addAll(sarif.runs.first().results)
            }
        }
        objectMapper.writeValue(output, mergedSarif)
    }
}
