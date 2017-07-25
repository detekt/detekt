package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@Suppress("LongParameterList")
data class ProcessingSettings(val project: Path,
							  val config: Config = Config.empty,
							  val pathFilters: List<PathFilter> = listOf(),
							  val parallelCompilation: Boolean = false,
							  val excludeDefaultRuleSets: Boolean = false,
							  val pluginPaths: List<Path> = emptyList()) {

	init {
		pluginPaths.forEach {
			require(Files.exists(it) && it.toString().endsWith("jar")) {
				"Given rule set $it does not exist or has no jar ending!"
			}
		}
	}

	val pluginUrls = pluginPaths.map { it.toUri().toURL() }.toTypedArray()
}
