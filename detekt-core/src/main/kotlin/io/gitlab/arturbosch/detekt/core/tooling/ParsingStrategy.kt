package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

typealias ParsingStrategy = (spec: ProcessingSpec, settings: ProcessingSettings) -> List<KtFile>

val contentToKtFile: (content: String, path: Path) -> ParsingStrategy = { content, path ->
    { spec, settings ->
        listOf(
            KtCompiler(settings.environment)
                .createKtFile(content, spec.projectSpec.basePath ?: path, path)
        )
    }
}

val pathToKtFile: (path: Path) -> ParsingStrategy = { path ->
    { spec, settings ->
        listOf(
            KtCompiler(settings.environment)
                .compile(spec.projectSpec.basePath ?: path, path)
        )
    }
}

val inputPathsToKtFiles: ParsingStrategy = { spec, settings ->
    val compiler = KtTreeCompiler(settings, spec.projectSpec)
    spec.projectSpec.inputPaths.flatMap(compiler::compile)
}
