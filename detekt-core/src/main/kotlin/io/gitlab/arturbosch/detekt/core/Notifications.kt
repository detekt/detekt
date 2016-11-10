package io.gitlab.arturbosch.detekt.core

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
interface Notification {
	val message: String
}

class ModificationNotification(path: Path) : Notification {
	override val message: String

	init {
		message = "File $path was modified."
	}

	override fun toString(): String {
		return message
	}
}