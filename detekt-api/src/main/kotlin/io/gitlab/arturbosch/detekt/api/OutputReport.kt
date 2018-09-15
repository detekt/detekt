package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
abstract class OutputReport : Extension {

	abstract val ending: String

	open val name
		get() = this::class.simpleName

	fun write(filePath: Path, detektion: Detektion) {
		val reportData = render(detektion)
		if (reportData != null) {
			assert(filePath.fileName.toString().endsWith(ending)) {
				"The $name needs to have a file ending of type .$ending, but was ${filePath.fileName}."
			}
			filePath.parent?.let { Files.createDirectories(it) }
			Files.write(filePath, reportData.toByteArray())
		}
	}

	abstract fun render(detektion: Detektion): String?
}
