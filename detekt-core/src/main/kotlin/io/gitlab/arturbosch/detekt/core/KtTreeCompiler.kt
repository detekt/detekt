package io.gitlab.arturbosch.detekt.core

import io.github.detekt.parser.KtCompiler
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.stream.consumeAsFlow
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.coroutineContext

class KtTreeCompiler(
    private val settings: ProcessingSettings,
    spec: ProjectSpec,
    private val compiler: KtCompiler = KtCompiler(settings.environment)
) {

    private val basePath: Path? = spec.basePath
    private val pathFilters: PathFilters? =
        PathFilters.of(spec.includes.toList(), spec.excludes.toList())

    suspend fun compile(path: Path): Flow<KtFile> {
        require(Files.exists(path)) { "Given path $path does not exist!" }
        return when {
            path.isFile() && path.isKotlinFile() -> flowOf(compiler.compile(basePath ?: path, path))
            path.isDirectory() -> compileProject(path)
            else -> {
                settings.info("Ignoring a file detekt cannot handle: $path")
                emptyFlow()
            }
        }
    }

    private suspend fun compileProject(project: Path): Flow<KtFile> {
        val coroutineContext = if (settings.spec.executionSpec.parallelParsing) {
            settings.taskPool.asCoroutineDispatcher()
        } else {
            coroutineContext
        }
        return flow<Path> { emitAll(Files.walk(project).consumeAsFlow()) }
            .filter { it.isFile() }
            .flowOn(Dispatchers.IO)
            .filter { it.isKotlinFile() }
            .filter { !isIgnored(it) }
            .map { path ->
                GlobalScope.async(coroutineContext) {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                        compiler.compile(basePath ?: project, path)
                    } catch (ex: Throwable) {
                        settings.error("Could not compile '$path'.", ex)
                        null
                    }
                }
            }
            .buffer()
            .mapNotNull { it.await() }
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
