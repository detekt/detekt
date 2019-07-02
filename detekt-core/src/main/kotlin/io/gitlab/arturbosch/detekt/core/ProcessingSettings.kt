package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.createCompilerConfiguration
import io.gitlab.arturbosch.detekt.api.internal.createKotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

/**
 * Settings to be used by detekt.
 * If using a custom executor service be aware that detekt won't shut it down after use!
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
@Suppress("LongParameterList")
data class ProcessingSettings @JvmOverloads constructor(
    val inputPaths: List<Path>,
    val config: Config = Config.empty,
    val pathFilters: PathFilters? = null,
    val parallelCompilation: Boolean = false,
    val excludeDefaultRuleSets: Boolean = false,
    val pluginPaths: List<Path> = emptyList(),
    val classpath: List<String> = emptyList(),
    val languageVersion: LanguageVersion = LanguageVersion.LATEST_STABLE,
    val jvmTarget: JvmTarget = JvmTarget.DEFAULT,
    val executorService: ExecutorService = ForkJoinPool.commonPool(),
    val outPrinter: PrintStream = System.out,
    val errorPrinter: PrintStream = System.err,
    val autoCorrect: Boolean = false,
    val debug: Boolean = false
) {
    /**
     * Single project input path constructor.
     */
    constructor(
        inputPath: Path,
        config: Config = Config.empty,
        pathFilters: PathFilters? = null,
        parallelCompilation: Boolean = false,
        excludeDefaultRuleSets: Boolean = false,
        pluginPaths: List<Path> = emptyList(),
        classpath: List<String> = emptyList(),
        languageVersion: LanguageVersion = LanguageVersion.LATEST_STABLE,
        jvmTarget: JvmTarget = JvmTarget.DEFAULT,
        executorService: ExecutorService = ForkJoinPool.commonPool(),
        outPrinter: PrintStream = System.out,
        errorPrinter: PrintStream = System.err,
        autoCorrect: Boolean = false,
        debug: Boolean = false
    ) : this(
        listOf(inputPath),
        config,
        pathFilters,
        parallelCompilation,
        excludeDefaultRuleSets,
        pluginPaths,
        classpath,
        languageVersion,
        jvmTarget,
        executorService,
        outPrinter,
        errorPrinter,
        autoCorrect,
        debug
    )

    init {
        pluginPaths.forEach {
            require(Files.exists(it)) { "Given plugin ‘$it’ does not exist." }
            require(it.toString().endsWith("jar")) { "Given plugin ‘$it’ is not a JAR." }
        }
    }

    val pluginUrls = pluginPaths.map { it.toUri().toURL() }.toTypedArray()

    /**
     * Lazily instantiates a Kotlin environment which can be shared between compiling and
     * analyzing logic.
     */
    val environment: KotlinCoreEnvironment by lazy {
        val compilerConfiguration = createCompilerConfiguration(inputPaths, classpath, languageVersion, jvmTarget)
        createKotlinCoreEnvironment(compilerConfiguration)
    }

    fun info(msg: String) = outPrinter.println(msg)

    fun error(msg: String, error: Throwable) {
        errorPrinter.println(msg)
        error.printStacktraceRecursively(errorPrinter)
    }

    fun debug(msg: () -> String) {
        if (debug) {
            outPrinter.println(msg())
        }
    }
}
