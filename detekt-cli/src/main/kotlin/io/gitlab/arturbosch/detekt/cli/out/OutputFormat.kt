package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Finding
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class OutputFormat(val report: Path) {

	fun create(smells: List<Finding>) {
		report.parent?.let { Files.createDirectories(it) }
		val smellData = smells.map { it.compactWithSignature() }.joinToString("\n")
		Files.write(report, smellData.toByteArray())
		println("Successfully wrote findings to $report")

	}

}