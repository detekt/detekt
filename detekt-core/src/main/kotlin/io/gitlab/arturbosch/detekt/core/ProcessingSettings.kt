package io.gitlab.arturbosch.detekt.core

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.PropertiesAware
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.core.settings.ClassloaderAware
import io.gitlab.arturbosch.detekt.core.settings.EnvironmentAware
import io.gitlab.arturbosch.detekt.core.settings.EnvironmentFacade
import io.gitlab.arturbosch.detekt.core.settings.ExtensionFacade
import io.gitlab.arturbosch.detekt.core.settings.LoggingAware
import io.gitlab.arturbosch.detekt.core.settings.LoggingFacade
import io.gitlab.arturbosch.detekt.core.settings.PropertiesFacade
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jetbrains.kotlin.utils.closeQuietly
import java.io.Closeable
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.ExecutorService

/**
 * Settings to be used by the detekt engine.
 *
 * Always close the settings as dispose the Kotlin compiler and detekt class loader.
 * If using a custom executor service be aware that detekt won't shut it down after use!
 */
@OptIn(UnstableApi::class)
class ProcessingSettings @Suppress("LongParameterList") constructor(
    val inputPaths: List<Path>,
    override val config: Config = Config.empty,
    val executorService: ExecutorService? = null,
    val autoCorrect: Boolean = false,
    override val configUris: Collection<URI> = emptyList(),
    val spec: ProcessingSpec
) : AutoCloseable, Closeable,
    LoggingAware by LoggingFacade(spec.loggingSpec),
    PropertiesAware by PropertiesFacade(),
    EnvironmentAware by EnvironmentFacade(spec.projectSpec, spec.compilerSpec),
    ClassloaderAware by ExtensionFacade(spec.extensionsSpec),
    SetupContext {

    val taskPool: TaskPool by lazy { TaskPool(executorService) }

    override fun close() {
        closeQuietly(taskPool)
        Disposer.dispose(disposable)
        pluginLoader.safeAs<URLClassLoader>()
            ?.let { closeQuietly(it) }
    }
}
