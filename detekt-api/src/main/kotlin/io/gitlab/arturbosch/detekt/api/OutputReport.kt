package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
abstract class OutputReport : Extension {

	abstract val ending: String

	fun write(filePath: Path, detektion: Detektion) {
		val reportData = render(detektion)
		if (reportData != null) {
			assert(filePath.endsWith(ending)) {
				"The ${ending.toUpperCase()} report needs to have a file ending of type .$ending."
			}
			filePath.parent?.let { Files.createDirectories(it) }
			Files.write(filePath, reportData.toByteArray())
		}
	}

	abstract fun render(detektion: Detektion): String?
}
