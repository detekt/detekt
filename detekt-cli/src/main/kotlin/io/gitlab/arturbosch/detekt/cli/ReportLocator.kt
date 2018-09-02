package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class ReportLocator(private val settings: ProcessingSettings) {

	private val consoleSubConfig = settings.config.subConfig("console-reports")
	private val consoleActive = consoleSubConfig.valueOrDefault(ACTIVE, true)
	private val consoleExcludes = consoleSubConfig.valueOrDefault(EXCLUDE, emptyList<String>())

	fun load(): List<Extension> {
		LOG.debug("console-report=$consoleActive")
		val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)
		val consoleReports = loadConsoleReports(detektLoader)
		LOG.debug { "ConsoleReports: $consoleReports" }
		return consoleReports
	}

	private fun loadConsoleReports(detektLoader: URLClassLoader) =
			if (consoleActive) {
				ServiceLoader.load(ConsoleReport::class.java, detektLoader)
						.filter { it.id !in consoleExcludes }
						.toList()
			} else {
				emptyList<ConsoleReport>()
			}

	companion object {
		private const val ACTIVE = "active"
		private const val EXCLUDE = "exclude"
	}
}
