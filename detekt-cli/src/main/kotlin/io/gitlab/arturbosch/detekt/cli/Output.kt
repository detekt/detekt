package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.Notification
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Output(detektion: Detektion) {

	private val findings: Map<String, List<Finding>> = detektion.findings
	private val notifications: List<Notification> = detektion.notifications

	init {
		printNotifications()
		printFindings()
	}

	fun write(outputPath: Path?) {
		if (outputPath != null) {
			outputPath.createParentFoldersIfNeeded()
			val smellData = findings
					.flatMap { it.value }
					.map { it.compact() }
					.joinToString("\n")
			Files.write(outputPath, smellData.toByteArray())
			println("\n Successfully wrote findings to $outputPath")
		}
	}

	private fun printNotifications() {
		notifications.forEach(::println)
		println()
	}

	private fun printFindings() {
		findings.forEach {
			it.key.print("Ruleset: ")
			it.value.forEach { it.compact().print("\t") }
		}
	}

	private fun Path.createParentFoldersIfNeeded() {
		val parent = this.parent
		Files.createDirectories(parent)
	}

}

