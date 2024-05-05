package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.isRegularFile

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

fun contentToKtFile(content: String, path: Path): ParsingStrategy = { settings ->
    require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }
    listOf(
        KtCompiler(settings.environment).createKtFile(content, path)
    )
}

val inputPathsToKtFiles: ParsingStrategy = { settings ->
    val compiler = KtCompiler(settings.environment)
    settings.spec.projectSpec.inputPaths.map { path ->
        compiler.compile(path)
    }
}
