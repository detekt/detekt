package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.Detekt
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
object Runner {

	fun runWith(main: Main) {
		val pathFilters = with(Main) { main.filters.letIf { split(";").map(::PathFilter) } }
		val rules = with(Main) { main.rules.letIf { split(";").map { Paths.get(it) } } }
		val config = loadConfiguration(main.config, main)

		val start = System.currentTimeMillis()
		val detektion = Detekt(main.project, config,
				rules, pathFilters, main.parallel,
				listOf(DetektProgressListener())
		).run()
		Output(detektion, main).report()
		val end = System.currentTimeMillis() - start
		println("\ndetekt run within $end ms")

		SmellBorder(config, main).check(detektion)
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
					"useTabs" -> (main.useTabs) as T
					else -> default()
				}
			}
		} else Config.empty
	}

	private fun <T> String?.letIf(init: String.() -> List<T>): List<T> =
			if (this == null || this.isEmpty()) listOf<T>() else this.init()

}