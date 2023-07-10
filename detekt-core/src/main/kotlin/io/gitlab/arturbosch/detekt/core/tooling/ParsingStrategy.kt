package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path

typealias ParsingStrategy = (settings: ProcessingSettings) -> List<KtFile>

fun contentToKtFile(content: String, path: Path): ParsingStrategy = { settings ->
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
    val pathFilters: PathFilters? =
        PathFilters.of(settings.spec.projectSpec.includes.toList(), settings.spec.projectSpec.excludes.toList())

    fun isIgnored(path: Path): Boolean {
        val ignored = pathFilters?.isIgnored(path)
        if (ignored == true) {
            settings.debug { "Ignoring file '$path'" }
        }
        return ignored ?: false
    }

    settings.environment.getSourceFiles()
        .filter { !isIgnored(Path(it.virtualFilePath)) }
}
