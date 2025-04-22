package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

val inputPathsToKtFiles: ParsingStrategy = { settings ->
    val compiler = KtCompiler(settings.spec.projectSpec.inputPaths, settings.environment.configuration)
    compiler.files
}
