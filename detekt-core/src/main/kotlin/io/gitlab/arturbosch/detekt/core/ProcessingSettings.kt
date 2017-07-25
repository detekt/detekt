package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
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
							  val ruleSets: List<Path> = emptyList(),
							  val changeListeners: List<FileProcessListener> = emptyList())
