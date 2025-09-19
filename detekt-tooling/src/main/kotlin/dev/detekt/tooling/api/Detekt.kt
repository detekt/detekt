package dev.detekt.tooling.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * Instance of detekt.
 *
 * Runs analysis based on [dev.detekt.tooling.api.spec.ProcessingSpec] configuration.
 */
interface Detekt {

    // Used by detekt-cli
    fun run(): AnalysisResult

    // Used by detekt-compiler-plugin
    fun run(files: Collection<KtFile>): AnalysisResult
}
