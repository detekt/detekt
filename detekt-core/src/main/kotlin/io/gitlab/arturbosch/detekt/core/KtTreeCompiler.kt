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
    private val settings: ProcessingSettings
) {

    companion object {
        val KT_ENDINGS = setOf("kt", "kts")
        fun instance(settings: ProcessingSettings): KtTreeCompiler = KtTreeCompiler(KtCompiler(), settings)
    }

    fun compile(path: Path): List<KtFile> {
        require(Files.exists(path)) { "Given path $path does not exist!" }
        return when {
            path.isFile() && path.isKotlinFile() -> listOf(compiler.compile(path, path))
            path.isDirectory() -> compileInternal(path)
            else -> {
                if (settings.debug) {
                    println("Ignoring a file detekt cannot handle: $path")
                }
                emptyList()
            }
        }
    }

    private fun streamFor(project: Path) =
        Files.walk(project).apply { if (settings.parallelCompilation) parallel() }

    private fun compileInternal(project: Path): List<KtFile> = streamFor(project)
        .filter(Path::isFile)
        .filter { it.isKotlinFile() }
        .filter { !ignored(it) }
        .map { compiler.compile(project, it) }
        .collect(Collectors.toList())

    private fun Path.isKotlinFile(): Boolean {
        val fullPath = toAbsolutePath().toString()
        val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
        return kotlinEnding in KT_ENDINGS
    }

    private fun ignored(path: Path): Boolean {
        val matchers = settings.pathFilters ?: return false
        return matchers.isIgnored(path)
    }
}
