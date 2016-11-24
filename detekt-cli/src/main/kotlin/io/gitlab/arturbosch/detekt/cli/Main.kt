package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Main {

	@Parameter(names = arrayOf("--project", "-p"), required = true,
			converter = PathConverter::class, description = "Project path to analyze (path/to/project).")
	lateinit var project: Path

	@Parameter(names = arrayOf("--filters", "-f"), description = "Path filters defined through regex with separator ';' (\".*test.*\").")
	val filters: String? = null // Using a converter for List<PathFilter> resulted into a ClassCastException

	@Parameter(names = arrayOf("--config", "-c"), description = "Path to the config file (path/to/config).",
			converter = PathConverter::class)
	var config: Path? = null

	@Parameter(names = arrayOf("--rules", "-r"), description = "Extra paths to ruleset jars separated by ';'.")
	val rules: String? = null

	@Parameter(names = arrayOf("--format"), description = "Enables formatting of source code. Cannot be used together with --config.")
	val formatting: Boolean = false

	@Parameter(names = arrayOf("--parallel"), description = "Enables parallel compilation of source files. Should only be used if the analyzing project has more than ~200 kotlin files.")
	val parallel: Boolean = false

	@Parameter(names = arrayOf("--useTabs"), description = "Tells the formatter that indentation with tabs are valid.")
	val useTabs: Boolean = false

	@Parameter(names = arrayOf("--help", "-h"), help = true, description = "Shows the usage.")
	private var help: Boolean = false

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			val main = parseAndValidateArgs(args)
			Runner().runWith(main)
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

