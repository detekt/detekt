package dev.detekt.tooling.api

/**
 * Instance of detekt.
 *
 * Runs analysis based on [dev.detekt.tooling.api.spec.ProcessingSpec] configuration.
 */
interface Detekt {

    // Used by detekt-cli
    fun run(): AnalysisResult
}
