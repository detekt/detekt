package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.BASELINE_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.CONFIG_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.CREATE_BASELINE_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.DEBUG_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.DISABLE_DEFAULT_RULESETS_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.FILTERS_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.GENERATE_CONFIG_PARAMETER
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

		val argumentList = baseDetektParameters(detekt)

		invokeCli(project, classpath, argumentList.toList())
	}

	fun createBaseline(detekt: Detekt) {
		val project = detekt.project
		val classpath = project.configurations.getAt("detekt")

		val argumentList = baseDetektParameters(detekt)
		argumentList += CREATE_BASELINE_PARAMETER

		invokeCli(project, classpath, argumentList.toList())
	}

	fun generateConfig(detekt: Detekt) {
		val project = detekt.project
		val classpath = project.configurations.getAt("detekt")

		val args = mapOf<String, String>(
				INPUT_PARAMETER to detekt.source.asFileTree.asPath
		)

		val argumentList = args.flatMapTo(ArrayList()) { listOf(it.key, it.value) }
		argumentList += GENERATE_CONFIG_PARAMETER

		invokeCli(project, classpath, argumentList.toList())
	}

	private fun baseDetektParameters(detekt: Detekt): MutableList<String> {
		val args = mutableMapOf<String, String>(
				INPUT_PARAMETER to detekt.source.asFileTree.asPath
		)

		detekt.config.orNull?.let { args += CONFIG_PARAMETER to it.asFile().absolutePath }
		detekt.filters.orNull?.let { args += FILTERS_PARAMETER to it }
		detekt.plugins.orNull?.let { args += PLUGINS_PARAMETER to it }
		detekt.baseline.orNull?.let { args += BASELINE_PARAMETER to it.asFile.absolutePath }

		if (detekt.reports.html.isEnabled) args += REPORT_HTML_PARAMETER to detekt.reports.html.destination.absolutePath
		if (detekt.reports.xml.isEnabled) args += REPORT_XML_PARAMETER to detekt.reports.xml.destination.absolutePath

		val argumentList = args.flatMapTo(ArrayList()) { listOf(it.key, it.value) }
		if (detekt.debug.get()) argumentList += DEBUG_PARAMETER
		if (detekt.parallel.get()) argumentList += PARALLEL_PARAMETER
		if (detekt.disableDefaultRuleSets.get()) argumentList += DISABLE_DEFAULT_RULESETS_PARAMETER

		return argumentList
	}

	private fun invokeCli(project: Project, classpath: Configuration, args: Iterable<String>) {
		Log.info(args)
		project.javaexec {
			main = "io.gitlab.arturbosch.detekt.cli.Main"
			classpath(classpath)
			args(args)
		}
	}
}
