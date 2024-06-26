package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KotlinFirLoader
import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

val inputPathsToKtFiles: ParsingStrategy = { settings ->
    KotlinFirLoader(
        sources = settings.spec.projectSpec.inputPaths.map { it.toFile() },
        classpath = settings.classpath.map { File(it) },
        KtCompiler(settings.environment),
    )
        .use { it.load() }
        .outputs
        .flatMap { it.fir }
        .onEach { println(it.source) }
        .map { firFile -> firFile.psi!! as KtFile }
}
