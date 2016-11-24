package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.Detekt
import io.gitlab.arturbosch.detekt.core.Notification
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */
class Runner {

	fun runWith(main: Main) {
		val pathFilters = with(Main) { main.filters.letIf { split(";").map(::PathFilter) } }
		val rules = with(Main) { main.rules.letIf { split(";").map { Paths.get(it) } } }
		val config = loadConfiguration(main.config, main)
		measureTimeMillis {
			val detektion = Detekt(main.project, config, rules, pathFilters, main.parallel).run()
			printModifications(detektion.notifications)
			printFindings(detektion.findings)
		}.let { println("\ndetekt run within $it ms") }
	}

	private fun loadConfiguration(configPath: Path?, main: Main): Config {
		return if (configPath != null) YamlConfig.load(configPath)
		else if (main.formatting) object : Config {
			override fun subConfig(key: String): Config {
				return this
			}

			override fun <T : Any> valueOrDefault(key: String, default: () -> T): T {
				@Suppress("UNCHECKED_CAST")
				return when (key) {
					"autoCorrect" -> true as T
					"useTabs" -> (if (main.useTabs) true else false) as T
					else -> default()
				}
			}
		} else Config.empty
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