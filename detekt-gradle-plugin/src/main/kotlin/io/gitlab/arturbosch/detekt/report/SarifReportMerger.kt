package io.gitlab.arturbosch.detekt.report

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File

private typealias JsonObject = MutableMap<String, Any?>

/**
 * A naive implementation to merge SARIF assuming all inputs are written by detekt.
 */
internal object SarifReportMerger {

    fun merge(inputs: Collection<File>, output: File) {
        val sarifs = inputs.filter { it.exists() }.map {
            @Suppress("UNCHECKED_CAST")
            (JsonSlurper().parse(it) as JsonObject)
        }
        val runsByTool = sarifs.flatMap { it.runs }.groupBy { it.tool.driver.fullName }

        val mergedRunsByTool = runsByTool.mapValues { (_, runs) ->
            val mergedResults = runs.flatMap { it.results.orEmpty() }
            val mergedRules = runs.flatMap { it.tool.driver.rules.orEmpty() }.distinctBy { it.id }
            
            runs[0].apply { 
                results = mergedResults
                tool.driver.rules = mergedRules
            }
        }

        val mergedSarif = sarifs[0].apply { this.runs = mergedRunsByTool.values.toList() }

        output.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mergedSarif)))
    }
}

private var JsonObject.runs: List<JsonObject>
    @Suppress("UNCHECKED_CAST")
    get() = this["runs"] as List<JsonObject>
    set(value) {
        this["runs"] = value
    }

private var JsonObject.results: List<JsonObject>?
    @Suppress("UNCHECKED_CAST")
    get() = this["results"] as List<JsonObject>?
    set(value) {
        this["results"] = value
    }

private val JsonObject.tool: JsonObject
    @Suppress("UNCHECKED_CAST")
    get() = this["tool"] as JsonObject

private val JsonObject.driver: JsonObject
    @Suppress("UNCHECKED_CAST")
    get() = this["driver"] as JsonObject

private val JsonObject.fullName: String
    @Suppress("UNCHECKED_CAST")
    get() = this["fullName"] as String

private val JsonObject.id: String
    @Suppress("UNCHECKED_CAST")
    get() = this["id"] as String

private var JsonObject.rules: List<JsonObject>?
    @Suppress("UNCHECKED_CAST")
    get() = this["rules"] as List<JsonObject>?
    set(value) {
        this["rules"] = value
    }
