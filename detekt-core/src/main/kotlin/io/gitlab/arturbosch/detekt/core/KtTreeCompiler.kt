package io.gitlab.arturbosch.detekt.core

import io.github.detekt.parser.KtCompiler
import io.github.detekt.tooling.api.PathFilters
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.psi.KtFile
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class KtTreeCompiler(
    private val settings: ProcessingSettings,
    projectSpec: ProjectSpec,
    private val compiler: KtCompiler = KtCompiler(settings.environment)
) {

    private val basePath: Path = projectSpec.basePath
    private val pathFilters: PathFilters? =
        PathFilters.of(projectSpec.includes.toList(), projectSpec.excludes.toList())

    fun compile(path: Path): List<KtFile> {
        require(path.exists()) { "Given path $path does not exist!" }
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
        val visitor = CollectingFileVisitor()
        Files.walkFileTree(project, visitor)
        val kotlinFiles = visitor.collected
        return if (settings.spec.executionSpec.parallelParsing) {
            val service = settings.taskPool
            val tasks = kotlinFiles.map { path ->
                service.task { compiler.compile(basePath, path) }
                    .recover {
                        settings.error("Could not compile '$path'.", it)
                        null
                    }
            }.toList()
            return awaitAll(tasks).filterNotNull()
        } else {
            kotlinFiles.map { compiler.compile(basePath, it) }.toList()
        }
    }

    private fun Path.isKotlinFile() = extension in KT_ENDINGS

    private inner class CollectingFileVisitor : FileVisitor<Path> {
        private val _collected: MutableList<Path> = mutableListOf()
        val collected: List<Path>
            get() = _collected

        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            if (dir.isIgnored()) {
                settings.debug { "Ignoring subtree '$dir'." }
                return FileVisitResult.SKIP_SUBTREE
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            if (!file.isKotlinFile()) {
                return FileVisitResult.CONTINUE
            }
            if (file.isIgnored()) {
                settings.debug { "Ignoring file '$file'" }
            } else {
                _collected.add(file)
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
            settings.error("Error visiting file '$file'.", exc)
            throw exc
        }

        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            return if (exc == null) FileVisitResult.CONTINUE else throw exc
        }

        private fun Path.isIgnored(): Boolean {
            return pathFilters?.isIgnored(this) ?: false
        }
    }

    companion object {
        val KT_ENDINGS = setOf("kt", "kts")
    }
}
