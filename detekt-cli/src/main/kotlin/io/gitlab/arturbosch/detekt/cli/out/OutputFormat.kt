package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Finding
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class OutputFormat(reportsPath: Path) {

	companion object {
		private const val OUTPUT_FILE = "report.detekt"
	}

	private val outputPath = reportsPath.resolve(OUTPUT_FILE)

	fun create(smells: List<Finding>) {
		val smellData = smells.map { it.compactWithSignature() }.joinToString("\n")
		Files.write(outputPath, smellData.toByteArray())
		println("Successfully wrote findings to $outputPath")

	}

}