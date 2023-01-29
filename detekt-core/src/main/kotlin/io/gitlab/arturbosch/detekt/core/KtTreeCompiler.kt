package io.gitlab.arturbosch.detekt.core

import io.github.detekt.parser.KtCompiler
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

class KtTreeCompiler(
    private val settings: ProcessingSettings,
    projectSpec: ProjectSpec,
    private val compiler: KtCompiler = KtCompiler(settings.environment)
) {

    private val basePath: Path? = projectSpec.basePath
    private val pathFilters: PathFilters? =
        PathFilters.of(projectSpec.includes.toList(), projectSpec.excludes.toList())

    fun compile(path: Path): List<KtFile> {
        require(Files.exists(path)) { "Given path $path does not exist!" }
        return when {
            path.isRegularFile() && path.isKotlinFile() -> listOf(compiler.compile(basePath, path))
            path.isDirectory() -> compileProject(path)
            else -> {
                settings.debug { "Ignoring a file detekt cannot handle: $path" }
                emptyList()
            }
        }
    }

    private fun compileProject(project: Path): List<KtFile> {
        val kotlinFiles = Files.walk(project)
            .asSequence()
            .filter(Path::isRegularFile)
            .filter { it.isKotlinFile() }
            .filter { !isIgnored(it) }
        return if (settings.spec.executionSpec.parallelParsing) {
            val service = settings.taskPool
            val tasks = kotlinFiles.map { path ->
                service.task { compiler.compile(basePath, path) }
                    .recover { settings.error("Could not compile '$path'.", it); null }
            }.toList()
            return awaitAll(tasks).filterNotNull()
        } else {
            kotlinFiles.map { compiler.compile(basePath, it) }.toList()
        }
    }

    private fun Path.isKotlinFile() = extension in KT_ENDINGS

    private fun isIgnored(path: Path): Boolean {
        val ignored = pathFilters?.isIgnored(path)
        if (ignored == true) {
            settings.debug { "Ignoring file '$path'" }
        }
        return ignored ?: false
    }

    companion object {
        val KT_ENDINGS = setOf("kt", "kts")
    }
}
