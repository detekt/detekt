package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
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
data class ProcessingSettings(val inputPaths: List<Path>,
							  val config: Config = Config.empty,
							  val pathFilters: List<PathFilter> = listOf(),
							  val parallelCompilation: Boolean = false,
							  val excludeDefaultRuleSets: Boolean = false,
							  val pluginPaths: List<Path> = emptyList(),
							  val executorService: ExecutorService? = ForkJoinPool.commonPool(),
							  val outPrinter: PrintStream = System.out,
							  val errorPrinter: PrintStream = System.err) {

	/**
	 * Single project input path constructor.
	 */
	constructor(
			inputPath: Path,
			config: Config = Config.empty,
			pathFilters: List<PathFilter> = listOf(),
			parallelCompilation: Boolean = false,
			excludeDefaultRuleSets: Boolean = false,
			pluginPaths: List<Path> = emptyList(),
			executorService: ExecutorService? = ForkJoinPool.commonPool(),
			outPrinter: PrintStream = System.out,
			errorPrinter: PrintStream = System.err
	) : this(
			listOf(inputPath), config, pathFilters, parallelCompilation,
			excludeDefaultRuleSets, pluginPaths, executorService, outPrinter, errorPrinter
	)

	init {
		pluginPaths.forEach {
			require(Files.exists(it)) { "Given plugin ‘$it’ does not exist." }
			require(it.toString().endsWith("jar")) { "Given plugin ‘$it’ is not a JAR." }
		}
	}

	val pluginUrls = pluginPaths.map { it.toUri().toURL() }.toTypedArray()

	fun loadTestPattern() = createTestPattern(config)
}
