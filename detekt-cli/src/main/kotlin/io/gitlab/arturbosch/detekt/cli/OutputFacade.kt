package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.cli.console.BuildFailureReport
import io.gitlab.arturbosch.detekt.cli.console.ComplexityReport
import io.gitlab.arturbosch.detekt.cli.console.FindingsReport
import io.gitlab.arturbosch.detekt.cli.console.NotificationReport
import io.gitlab.arturbosch.detekt.cli.console.ProjectStatisticsReport
import io.gitlab.arturbosch.detekt.cli.out.Formatter

/**
 * @author Artur Bosch
 */
class OutputFacade(args: Main, private val detektion: Detektion) {

	private val reportPath = args.output
	private val outputFormatter: Formatter = args.outputFormatter
	private val findings: Map<String, List<Finding>> = detektion.findings
	private val baselineFacade = args.baseline?.let { BaselineFacade(it) }
	private val createBaseline = args.createBaseline

	fun consoleFacade() {
		val out = System.out
		NotificationReport().print(out, detektion)
		FindingsReport().print(out, detektion)
		ComplexityReport().print(out, detektion)
		ProjectStatisticsReport().print(out, detektion)
		BuildFailureReport().print(out, detektion)
	}

	fun reportFacade() {
		val smells = findings.flatMap { it.value }
		reportPath?.let {
			outputFormatter.create().write(reportPath, detektion)
			println("Successfully wrote findings to $it")
		}
		if (createBaseline) baselineFacade?.create(smells)
	}
}
