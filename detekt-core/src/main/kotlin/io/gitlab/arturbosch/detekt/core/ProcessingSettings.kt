package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService

/**
 * Settings to be used by detekt.
 * If using a custom executor service be aware that detekt won't shutdown it after use!
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
@Suppress("LongParameterList")
data class ProcessingSettings(val project: List<Path>,
							  val config: Config = Config.empty,
							  val pathFilters: List<PathFilter> = listOf(),
							  val parallelCompilation: Boolean = false,
							  val excludeDefaultRuleSets: Boolean = false,
							  val pluginPaths: List<Path> = emptyList(),
							  val debug: Boolean = false,
							  val executorService: ExecutorService? = null) {

	constructor(project: Path,
				config: Config = Config.empty,
				pathFilters: List<PathFilter> = listOf(),
				parallelCompilation: Boolean = false,
				excludeDefaultRuleSets: Boolean = false,
				pluginPaths: List<Path> = emptyList(),
				debug: Boolean = false,
				executorService: ExecutorService? = null) :
			this(listOf(project), config, pathFilters, parallelCompilation,
					excludeDefaultRuleSets, pluginPaths, debug, executorService)

	init {
		pluginPaths.forEach {
			require(Files.exists(it) && it.toString().endsWith("jar")) {
				"Given plugin $it does not exist or end with jar!"
			}
		}
	}

	val pluginUrls = pluginPaths.map { it.toUri().toURL() }.toTypedArray()

	fun loadTestPattern() = createTestPattern(config)
}
