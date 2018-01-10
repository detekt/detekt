package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Notification
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class ModificationNotification(path: Path) : Notification {

	override val message: String = "File $path was modified."
	override fun toString(): String = message
}
