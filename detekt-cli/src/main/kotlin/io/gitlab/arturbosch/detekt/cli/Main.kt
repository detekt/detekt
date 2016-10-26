package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.Detekt
import io.gitlab.arturbosch.detekt.core.PathFilter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
private class Main {

	@Parameter(names = arrayOf("--project", "-p"), required = true,
			converter = PathConverter::class, description = "Project path to analyze (path/to/project).")
	private lateinit var project: Path

	@Parameter(names = arrayOf("--filters", "-f"), description = "Path filters defined through regex with separator ';' (\".*test.*\").")
	private val filters: String? = null // Using a converter for List<PathFilter> resulted into a ClassCastException

	@Parameter(names = arrayOf("--config", "-c"), description = "Path to the config file (path/to/config).",
			converter = PathConverter::class)
	private var config: Path? = null

	@Parameter(names = arrayOf("--rules", "-r"), description = "Extra paths to ruleset jars separated by ';'.")
	private val rules: String? = null

	@Parameter(names = arrayOf("--help", "-h"), help = true, description = "Shows the usage.")
	private var help: Boolean = false

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			val cli = parseAndValidateArgs(args)
			val pathFilters = cli.filters?.split(";")?.map(::PathFilter) ?: listOf()
			val rules = cli.rules?.split(";")?.map { Paths.get(it) } ?: listOf()
			val configPath = cli.config
			val config = if (configPath != null) YamlConfig.load(configPath) else Config.EMPTY
			val results = Detekt(cli.project, config, rules, pathFilters = pathFilters).run()
			printFindings(results)
		}

		private fun parseAndValidateArgs(args: Array<String>): Main {
			val cli = Main()
			val jCommander = JCommander(cli)
			jCommander.setProgramName("detekt")

			try {
				jCommander.parse(*args)
			} catch (ex: ParameterException) {
				println(ex.message)
				println()
				jCommander.usage()
				System.exit(-1)
			}

			if (cli.help) {
				jCommander.usage()
				System.exit(-1)
			}
			return cli
		}
	}
}
