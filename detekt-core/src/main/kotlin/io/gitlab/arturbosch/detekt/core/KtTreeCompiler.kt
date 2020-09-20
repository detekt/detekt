package io.gitlab.arturbosch.detekt.core

import io.github.detekt.parser.KtCompiler
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class KtTreeCompiler(
    private val settings: ProcessingSettings,
    spec: ProjectSpec,
    private val compiler: KtCompiler = KtCompiler(settings.environment)
) {

    private val basePath: Path? = spec.basePath
    private val pathFilters: PathFilters? =
        PathFilters.of(spec.includes.toList(), spec.excludes.toList())

    fun compile(path: Path): List<KtFile> {
        require(Files.exists(path)) { "Given path $path does not exist!" }
        return when {
            path.isFile() && path.isKotlinFile() -> listOf(compiler.compile(basePath ?: path, path))
            path.isDirectory() -> compileProject(path)
            else -> {
                settings.info("Ignoring a file detekt cannot handle: $path")
                emptyList()
            }
        }
    }

    private fun compileProject(project: Path): List<KtFile> {
        val kotlinFiles = Files.walk(project)
            .filter(Path::isFile)
            .filter { it.isKotlinFile() }
            .filter { !isIgnored(it) }
        return if (settings.spec.executionSpec.parallelParsing) {
            val service = settings.taskPool
            val tasks = kotlinFiles.map { path ->
                service.task { compiler.compile(basePath ?: project, path) }
                    .recover { settings.error("Could not compile '$path'.", it); null }
            }.collect(Collectors.toList())
            return awaitAll(tasks).filterNotNull()
        } else {
            kotlinFiles.map { compiler.compile(basePath ?: project, it) }.collect(Collectors.toList())
        }
    }

    private fun Path.isKotlinFile(): Boolean {
        val fullPath = toAbsolutePath().toString()
        val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
        return kotlinEnding in KT_ENDINGS
    }

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
