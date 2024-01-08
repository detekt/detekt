package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.isRegularFile

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

fun contentToKtFile(content: String, path: Path): ParsingStrategy = { settings ->
    require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }
    listOf(
        KtCompiler(settings.environment).createKtFile(content, settings.spec.projectSpec.basePath, path)
    )
}

fun pathToKtFile(path: Path): ParsingStrategy = { settings ->
    listOf(
        KtCompiler(settings.environment).compile(settings.spec.projectSpec.basePath, path)
    )
}

val inputPathsToKtFiles: ParsingStrategy = { settings ->
    val compiler = KtTreeCompiler(settings, settings.spec.projectSpec)
    settings.spec.projectSpec.inputPaths.flatMap(compiler::compile)
}
