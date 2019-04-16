package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class KtTreeCompiler(
    private val compiler: KtCompiler = KtCompiler(),
    private val filters: List<PathFilter> = listOf(),
    private val parallel: Boolean = false,
    private val debug: Boolean = false
) {

    companion object {
        fun instance(settings: ProcessingSettings) = with(settings) {
            KtTreeCompiler(KtCompiler(), pathFilters, parallelCompilation)
        }
    }

    fun compile(path: Path): List<KtFile> {
        require(Files.exists(path)) { "Given path $path does not exist!" }
        return when {
            path.isFile() && path.isKotlinFile() -> listOf(compiler.compile(path, path))
            path.isDirectory() -> compileInternal(path)
            else -> {
                if (debug) {
                    println("Ignoring a file detekt cannot handle: $path")
                }
                listOf()
            }
        }
    }

    private fun streamFor(project: Path) = Files.walk(project).apply { if (parallel) parallel() }

    private fun compileInternal(project: Path): List<KtFile> = streamFor(project)
            .filter(Path::isFile)
            .filter { it.isKotlinFile() }
            .filter { notIgnored(it) }
            .map { compiler.compile(project, it) }
            .collect(Collectors.toList())

    private fun Path.isKotlinFile(): Boolean {
        val fullPath = toAbsolutePath().toString()
        val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
        return kotlinEnding == "kt" || kotlinEnding == "kts"
    }

    private fun notIgnored(path: Path) = !filters.any { it.matches(path) }
}
