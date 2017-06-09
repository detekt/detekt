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

/**
 * @author Artur Bosch
 */
class OutputFacade(args: Main,
				   config: Config,
				   private val detektion: Detektion) {

	private val findings: Map<String, List<Finding>> = detektion.findings
	private val notifications: List<Notification> = detektion.notifications
	private val baselineFormat = args.baseline?.let { DetektBaselineFormat(it) }
	private val outputFormat = args.output?.let { OutputFormat(it) }
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
		val smells = findings.flatMap { it.value }
		println()
		outputFormat?.create(smells)
		baselineFormat?.create(smells)
	}

	fun buildErrorCheck() {
		smellThreshold.check(detektion)
	}
}
