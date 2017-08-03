package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
abstract class OutputReport : Extension {

	abstract var fileName: String
	abstract val ending: String

	fun write(report: Path, detektion: Detektion) {
		val smellData = render(detektion)
		smellData?.let {
			val filePath = report.resolve("$fileName.$ending")
			filePath.parent?.let { Files.createDirectories(it) }
			Files.write(filePath, it.toByteArray())
		}
	}

	abstract fun render(detektion: Detektion): String?
}
