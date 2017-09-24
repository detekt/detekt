package io.gitlab.arturbosch.detekt.watchservice

import java.nio.file.Path
import java.nio.file.WatchEvent

/**
 * @author Artur Bosch
 */
data class WatchedDir(val valid: Boolean,
					  val dir: Path,
					  val events: List<PathEvent>)

data class PathEvent(val path: Path, val kind: WatchEvent.Kind<*>)
