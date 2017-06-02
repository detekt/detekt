package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.out.DetektBaselineFormat
import io.gitlab.arturbosch.detekt.cli.out.OutputFormat
import io.gitlab.arturbosch.detekt.cli.out.SmellThreshold
import io.gitlab.arturbosch.detekt.core.COMPLEXITY_KEY
import io.gitlab.arturbosch.detekt.core.Detektion
import io.gitlab.arturbosch.detekt.core.LLOC_KEY
import io.gitlab.arturbosch.detekt.core.Notification
import io.gitlab.arturbosch.detekt.core.isDirectory
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class OutputFacade(args: Main,
				   config: Config,
				   private val detektion: Detektion) {

	private val generateOutput = args.output
	private val generateBaseline = args.baseline
	private val reportDirectory: Path? = args.reportDirectory
	private val findings: Map<String, List<Finding>> = detektion.findings
	private val notifications: List<Notification> = detektion.notifications
	private val baselineFormat = reportDirectory?.let { DetektBaselineFormat(it) }
	private val outputFormat = reportDirectory?.let { OutputFormat(it) }
	private val smellThreshold = SmellThreshold(config, baselineFormat)

	fun consoleFacade() {
		printNotifications()
		printFindings()
		printComplexity()
	}

	fun printNotifications() {
		for (notification in notifications) println(notification)
		println()
	}

	fun printFindings() {
		if (baselineFormat != null) println("Only new findings are printed as baseline.xml is found:\n")

		findings.forEach {
			it.key.print("Ruleset: ")
			val values = baselineFormat?.filter(it.value) ?: it.value
			values.forEach { it.compact().print("\t") }
		}
	}

	fun printComplexity() {
		val mcc = detektion.getData(COMPLEXITY_KEY)
		val lloc = detektion.getData(LLOC_KEY)
		if (mcc != null && lloc != null && lloc > 0) {
			val numberOfSmells = findings.entries.sumBy { it.value.size }
			val smellPerThousandLines = numberOfSmells * 1000 / lloc
			val mccPerThousandLines = mcc * 1000 / lloc
			println()
			println("Complexity Report:")
			println("\t- $lloc logical lines of code (lloc)")
			println("\t- $mcc McCabe complexity (mcc)")
			println("\t- $mccPerThousandLines mcc per 1000 lloc")
			println("\t- $smellPerThousandLines code smells per 1000 lloc")
		}
	}

	fun reportFacade() {
		if (reportDirectory != null) {
			reportDirectory.createFoldersIfNeeded()
			val smells = findings.flatMap { it.value }
			if (generateOutput || generateBaseline) println()
			if (generateOutput) outputFormat?.create(smells)
			if (generateBaseline) baselineFormat?.create(smells)
		}
	}

	fun buildErrorCheck() {
		smellThreshold.check(detektion)
	}

	private fun Path.createFoldersIfNeeded() {
		if (Files.exists(this) && !this.isDirectory()) {
			throw IllegalArgumentException("Report path must be a directory!")
		} else {
			Files.createDirectories(this)
		}
	}

}

