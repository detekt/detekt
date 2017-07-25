package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
abstract class OutputFormat {

	open val id: String = javaClass.simpleName
	open val priority: Int = -1

	fun write(report: Path, smells: List<Finding>) {
		val smellData = render(smells)
		smellData?.let {
			report.parent?.let { Files.createDirectories(it) }
			Files.write(report, it.toByteArray())
		}
	}

	abstract fun render(smells: List<Finding>): String?
}
