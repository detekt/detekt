package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.createCompilerConfiguration
import io.gitlab.arturbosch.detekt.api.internal.createKotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.utils.closeQuietly
import java.io.Closeable
import java.io.PrintStream
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Files
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
    val pathFilters: PathFilters? = null,
    val parallelCompilation: Boolean = false,
    val excludeDefaultRuleSets: Boolean = false,
    val pluginPaths: List<Path> = emptyList(),
    val classpath: List<String> = emptyList(),
    val languageVersion: LanguageVersion? = null,
    val jvmTarget: JvmTarget = JvmTarget.DEFAULT,
    val executorService: ExecutorService? = null,
    override val outPrinter: PrintStream,
    override val errPrinter: PrintStream,
    val autoCorrect: Boolean = false,
    val debug: Boolean = false,
    override val configUris: Collection<URI> = emptyList()
) : AutoCloseable, Closeable, SetupContext {

    init {
        pluginPaths.forEach {
            require(Files.exists(it)) { "Given plugin ‘$it’ does not exist." }
            require(it.toString().endsWith("jar")) { "Given plugin ‘$it’ is not a JAR." }
        }
    }

    private val environmentDisposable: Disposable = Disposer.newDisposable()

    /**
     * Shared class loader used to load services from plugin jars.
     */
    val pluginLoader: URLClassLoader by lazy {
        val pluginUrls = pluginPaths.map { it.toUri().toURL() }.toTypedArray()
        URLClassLoader(pluginUrls, javaClass.classLoader)
    }

    /**
     * Lazily instantiates a Kotlin environment which can be shared between compiling and
     * analyzing logic.
     */
    val environment: KotlinCoreEnvironment by lazy {
        val compilerConfiguration = createCompilerConfiguration(inputPaths, classpath, languageVersion, jvmTarget)
        createKotlinCoreEnvironment(compilerConfiguration, environmentDisposable)
    }

    val taskPool: TaskPool by lazy { TaskPool(executorService) }

    fun info(msg: String) = outPrinter.println(msg)

    fun error(msg: String, error: Throwable) {
        errPrinter.println(msg)
        error.printStacktraceRecursively(errPrinter)
    }

    fun debug(msg: () -> String) {
        if (debug) {
            outPrinter.println(msg())
        }
    }

    override fun close() {
        closeQuietly(taskPool)
        Disposer.dispose(environmentDisposable)
        closeQuietly(pluginLoader)
    }
}
