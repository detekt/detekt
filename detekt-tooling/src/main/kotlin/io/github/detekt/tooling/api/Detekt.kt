package io.github.detekt.tooling.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Instance of detekt.
 *
 * Runs analysis based on [io.github.detekt.tooling.api.spec.ProcessingSpec] configuration.
 */
interface Detekt {

    // Used by detekt-cli
    fun run(): AnalysisResult

    // Used by detekt-compiler-plugin
    fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult
}
