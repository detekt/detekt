package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputFormat
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class ReportLocator(private val settings: ProcessingSettings) {

	fun load(): List<Extension> {
		val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)
		val consoleReports = ServiceLoader.load(ConsoleReport::class.java, detektLoader)
		val outputReports = ServiceLoader.load(OutputFormat::class.java, detektLoader)
		return consoleReports.plus(outputReports)
	}
}
