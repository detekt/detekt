package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.jetbrains.kotlin.config.JvmTarget
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
}
