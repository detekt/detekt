package dev.detekt.core

import com.intellij.openapi.util.Disposer
import dev.detekt.api.Config
import dev.detekt.api.PropertiesAware
import dev.detekt.api.SetupContext
import dev.detekt.core.settings.ClassloaderAware
import dev.detekt.core.settings.EnvironmentAware
import dev.detekt.core.settings.EnvironmentFacade
import dev.detekt.core.settings.ExtensionFacade
import dev.detekt.core.settings.LoggingAware
import dev.detekt.core.settings.LoggingFacade
import dev.detekt.core.settings.PropertiesFacade
import dev.detekt.core.util.PerformanceMonitor
import dev.detekt.tooling.api.spec.ConfigSpec
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.jetbrains.kotlin.utils.closeQuietly
import java.io.Closeable
import java.net.URI
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * Settings to be used by the detekt engine.
 *
 * Always close the settings as dispose the Kotlin compiler and detekt class loader.
 * If using a custom executor service be aware that detekt won't shut it down after use!
 */
class ProcessingSettings(val spec: ProcessingSpec, override val config: Config, val monitor: PerformanceMonitor) :
    AutoCloseable,
    Closeable,
    LoggingAware by LoggingFacade(spec.loggingSpec),
    PropertiesAware by PropertiesFacade(),
    EnvironmentAware by EnvironmentFacade(spec.projectSpec, spec.compilerSpec, spec.loggingSpec),
    ClassloaderAware by ExtensionFacade(spec.extensionsSpec.plugins),
    SetupContext {

    override val configUris: Collection<URI> = spec.configSpec.extractUris()

    override val basePath: Path = spec.projectSpec.basePath

    /**
     * Sharable thread pool between parsing and analysis phase.
     */
    val taskPool: TaskPool by lazy { TaskPool(spec.executionSpec.executorService) }

    override fun close() {
        closeQuietly(taskPool)
        Disposer.dispose(disposable)
        closeLoaderIfNeeded()
    }
}

private fun ConfigSpec.extractUris(): Collection<URI> {
    fun initFileSystem(uri: URI) {
        runCatching {
            @Suppress("SwallowedException") // Create file system inferred from URI if it does not exist.
            try {
                FileSystems.getFileSystem(uri)
            } catch (e: FileSystemNotFoundException) {
                FileSystems.newFileSystem(uri, mapOf("create" to "true"))
            }
        }
    }

    val pathUris = configPaths.map(Path::toUri)
    val resourceUris = resources.map(URL::toURI)
    resourceUris.forEach(::initFileSystem)
    return resourceUris + pathUris
}
