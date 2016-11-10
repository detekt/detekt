package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.Detekt
import io.gitlab.arturbosch.detekt.core.Notification
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class Runner {

	fun runWith(main: Main) {
		val pathFilters = with(Main) { main.filters.letIf { split(";").map(::PathFilter) } }
		val rules = with(Main) { main.rules.letIf { split(";").map { Paths.get(it) } } }
		val configPath = main.config
		val config = if (configPath != null) YamlConfig.load(configPath) else Config.empty
		val detektion = Detekt(main.project, config, rules, pathFilters = pathFilters).run()
		printModifications(detektion.notifications)
		printFindings(detektion.findings)
	}

	private fun printModifications(notifications: List<Notification>) {
		notifications.forEach(::println)
		println()
	}

	private fun printFindings(result: Map<String, List<Finding>>) {
		result.forEach {
			it.key.print("Ruleset: ")
			it.value.each { it.compact().print("\t") }
		}
	}

	private fun <T> String?.letIf(init: String.() -> List<T>): List<T> =
			if (this == null || this.isEmpty()) listOf<T>() else this.init()

}