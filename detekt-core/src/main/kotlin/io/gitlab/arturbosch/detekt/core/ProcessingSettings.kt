package io.gitlab.arturbosch.detekt.core

/**
 * @author Artur Bosch
 */
data class ProcessingSettings(val pathFilters: List<PathFilter> = listOf(),
							  val parallelCompilation: Boolean = false,
							  val changeListeners: List<FileProcessListener> = emptyList())