package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
abstract class OutputFormat : Extension {

	fun write(report: Path, detektion: Detektion) {
		val smells = detektion.findings.flatMap { it.value }
		val smellData = render(smells)
		smellData?.let {
			report.parent?.let { Files.createDirectories(it) }
			Files.write(report, it.toByteArray())
		}
	}

	abstract fun render(smells: List<Finding>): String?
}
