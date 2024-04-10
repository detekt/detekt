package io.github.detekt.tooling.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Instance of detekt.
 *
 * Runs analysis based on [io.github.detekt.tooling.api.spec.ProcessingSpec] configuration.
 */
interface Detekt {

    fun run(): AnalysisResult

    fun run(sourceCode: String, filename: String): AnalysisResult

    fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult
}
