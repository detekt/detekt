package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Report
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class ReportLocator(private val settings: ProcessingSettings) {

	fun load(): List<Report> {
		val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)
		return ServiceLoader.load(Report::class.java, detektLoader).toList()
	}
}
