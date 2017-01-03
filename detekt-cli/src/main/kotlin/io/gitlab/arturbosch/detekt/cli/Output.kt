package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.Notification
import io.gitlab.arturbosch.detekt.core.isDirectory
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Output(detektion: Detektion, args: Main) {

	companion object {
		private const val OUTPUT_FILE = "report.detekt"
	}

	private val withOutput: Boolean = args.output
	private val withBaseline: Boolean = args.baseline
	private val reportDirectory: Path? = args.reportDirectory
	private val findings: Map<String, List<Finding>> = detektion.findings
	private val notifications: List<Notification> = detektion.notifications

	init {
		printNotifications()
		printFindings()
	}

	fun report() {
		if (reportDirectory != null) {
			reportDirectory.createFoldersIfNeeded()
			val smells = findings.flatMap { it.value }
			if (withOutput) {
				val smellData = smells.map { it.compactWithSignature() }.joinToString("\n")
				val reportFile = reportDirectory.resolve(OUTPUT_FILE)
				Files.write(reportFile, smellData.toByteArray())
				println("\n Successfully wrote findings to $reportFile")
			}
			if (withBaseline) {
				DetektBaselineFormat.create(smells, reportDirectory)
			}
		}
	}

	private fun printNotifications() {
		for (notification in notifications) println(notification)
		println()
	}

	private fun printFindings() {
		val listings = DetektBaselineFormat.listings(reportDirectory)
		if (listings != null) println("Only new findings are printed as baseline.xml is found:\n")

		findings.forEach {
			it.key.print("Ruleset: ")
			val values = it.value.filterListedFindings(listings)
			values.forEach { it.compact().print("\t") }
		}
	}

	private fun Path.createFoldersIfNeeded() {
		if (Files.exists(this) && !this.isDirectory()) {
			throw IllegalArgumentException("Report path must be a directory!")
		} else {
			Files.createDirectories(this)
		}
	}

}

