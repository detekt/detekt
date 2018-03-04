package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.BASELINE_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.CONFIG_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.DEBUG_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.DISABLE_DEFAULT_RULESETS_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.FILTERS_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.INPUT_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.PARALLEL_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.PLUGINS_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.REPORT_HTML_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.REPORT_XML_PARAMETER
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.org.jline.utils.Log

/**
 * @author Marvin Ramin
 */
object DetektInvoker {
	fun check(detekt: Detekt) {
		val project = detekt.project
		val classpath = project.configurations.getAt("detekt")

		val args = mutableMapOf<String, String>(
				INPUT_PARAMETER to detekt.source.asFileTree.asPath
		)

		detekt.config?.let { args += CONFIG_PARAMETER to it.asFile().absolutePath }
		detekt.filters?.let { args += FILTERS_PARAMETER to it }
		detekt.plugins?.let { args += PLUGINS_PARAMETER to it }
		detekt.baseline?.let { args += BASELINE_PARAMETER to it.absolutePath }

		if (detekt.reports.html.isEnabled) args += REPORT_HTML_PARAMETER to detekt.reports.html.destination.absolutePath
		if (detekt.reports.xml.isEnabled) args += REPORT_XML_PARAMETER to detekt.reports.xml.destination.absolutePath

		val argumentList = args.toArgumentList()
		if (detekt.debug) argumentList += DEBUG_PARAMETER
		if (detekt.parallel) argumentList += PARALLEL_PARAMETER
		if (detekt.disableDefaultRuleSets) argumentList += DISABLE_DEFAULT_RULESETS_PARAMETER

		Log.info(argumentList)
		invokeCli(project, classpath, argumentList.toList())
	}

	fun createBaseline() {
		// TODO baseline task
	}

	fun generateConfig() {
		// TODO generate config task
	}

	private fun invokeCli(project: Project, classpath: Configuration, args: Iterable<String>) {
		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = classpath
			it.args(args)
		}
	}

	private fun <T> Map<T, T>.toArgumentList(): MutableList<T> {
		val list = mutableListOf<T>()
		this.keys.forEach {
			list += it
			list += this.getValue(it)
		}
		return list
	}
}
