package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

fun contentToKtFile(content: String, path: Path): ParsingStrategy = { settings ->
    listOf(
        KtCompiler(settings.environment)
            .createKtFile(content, settings.spec.projectSpec.basePath ?: path, path)
    )
}

fun pathToKtFile(path: Path): ParsingStrategy = { settings ->
    listOf(
        KtCompiler(settings.environment)
            .compile(settings.spec.projectSpec.basePath ?: path, path)
    )
}

val inputPathsToKtFiles: ParsingStrategy = { settings ->
    val compiler = KtTreeCompiler(settings, settings.spec.projectSpec)
    settings.spec.projectSpec.inputPaths.flatMap(compiler::compile)
}
