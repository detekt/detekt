package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class ReportLocator(private val settings: ProcessingSettings) {

	private val consoleActive = settings.config
			.subConfig("console-reports")
			.valueOrDefault("active", true)

	private val outputActive = settings.config
			.subConfig("output-reports")
			.valueOrDefault("active", true)

	fun load(): List<Extension> {
		LOG.debug("console-report=$consoleActive")
		LOG.debug("output-report=$outputActive")
		val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)
		val consoleReports =
				if (consoleActive) ServiceLoader.load(ConsoleReport::class.java, detektLoader).toList()
				else emptyList<ConsoleReport>()
		LOG.debug { "ConsoleReports: $consoleReports" }
		val outputReports =
				if (outputActive) ServiceLoader.load(OutputReport::class.java, detektLoader).toList()
				else emptyList<OutputReport>()
		LOG.debug { "OutputReports: $outputReports" }
		return consoleReports.plus(outputReports)
	}
}
