package io.gitlab.arturbosch.detekt.report

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File

private typealias JsonObject = MutableMap<String, Any>

/**
 * A naive implementation to merge SARIF assuming all inputs are written by detekt.
 */
object SarifReportMerger {

    fun merge(inputs: Collection<File>, output: File) {
        val sarifs = inputs.filter { it.exists() }.map {
            @Suppress("UNCHECKED_CAST")
            (JsonSlurper().parse(it) as JsonObject)
        }
        val mergedResults = sarifs.flatMap { it.runs.single().results }
        val mergedSarif = sarifs[0].apply { this.runs.single().results = mergedResults }
        output.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mergedSarif)))
    }
}

private val JsonObject.runs: List<JsonObject>
    @Suppress("UNCHECKED_CAST")
    get() = this["runs"] as List<JsonObject>

private var JsonObject.results: List<JsonObject>
    @Suppress("UNCHECKED_CAST")
    get() = this["results"] as List<JsonObject>
    set(value) {
        this["results"] = value
    }
