package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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

@OptIn(FlowPreview::class)
val inputPathsToKtFiles: ParsingStrategy = { settings ->
    val compiler = KtTreeCompiler(settings, settings.spec.projectSpec)
    runBlocking {
        settings.spec.projectSpec.inputPaths
            .asFlow()
            .flatMapMerge { path -> compiler.compile(path) }
            .toList()
    }
}
