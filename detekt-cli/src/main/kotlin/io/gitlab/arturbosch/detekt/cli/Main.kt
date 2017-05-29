package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.Parameter
import io.gitlab.arturbosch.detekt.cli.debug.Debugger
import java.net.URL
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Main {

	@Parameter(names = arrayOf("--project", "-p"), required = true,
			converter = ExistingPathConverter::class, description = "Project path to analyze (path/to/project).")
	lateinit var project: Path

	@Parameter(names = arrayOf("--filters", "-f"), description = "Path filters defined through regex with separator ';' (\".*test.*\").")
	var filters: String? = null // Using a converter for List<PathFilter> resulted in a ClassCastException

	@Parameter(names = arrayOf("--config", "-c"), description = "Path to the config file (path/to/config.yml).",
			converter = ExistingPathConverter::class)
	var config: Path? = null

	@Parameter(names = arrayOf("--config-resource", "-cr"), description = "Path to the config resource on detekt's classpath (path/to/config.yml).",
			converter = ClasspathResourceConverter::class)
	var configResource: URL? = null

	@Parameter(names = arrayOf("--generate-config", "-gc"), help = true,
			description = "Export default config to default-detekt-config.yml.")
	var generateConfig: Boolean = false

	@Parameter(names = arrayOf("--report", "-rp"), description = "Path to the report directory where findings should be stored (if --output) and baseline.xml generated (if --baseline).",
			converter = PathConverter::class)
	var reportDirectory: Path? = null

	@Parameter(names = arrayOf("--rules", "-r"), description = "Extra paths to ruleset jars separated by ';'.")
	var rules: String? = null

	@Parameter(names = arrayOf("--format"), description = "Enables formatting of source code. Cannot be used together with --config.")
	var formatting: Boolean = false

	@Parameter(names = arrayOf("--parallel"), description = "Enables parallel compilation of source files. Should only be used if the analyzing project has more than ~200 kotlin files.")
	var parallel: Boolean = false

	@Parameter(names = arrayOf("--useTabs"), description = "Tells the formatter that indentation with tabs are valid.")
	var useTabs: Boolean = false

	@Parameter(names = arrayOf("--baseline", "-b"), description = "Treats current analysis findings as a smell baseline for further detekt runs. If a baseline xml file exists, only new code smells not in the baseline are printed in the console.")
	var baseline: Boolean = false

	@Parameter(names = arrayOf("--output", "-o"), description = "True if findings should be written into a report.detekt file inside the report folder.")
	var output: Boolean = false

	@Parameter(names = arrayOf("--disable-default-rulesets", "-dd"), description = "Disables default rule sets.")
	var disableDefaultRuleSets: Boolean = false

	@Parameter(names = arrayOf("--debug", "-d"), description = "Debugs given ktFile by printing its elements.")
	var debug: Boolean = false

	@Parameter(names = arrayOf("--help", "-h"), help = true, description = "Shows the usage.")
	var help: Boolean = false

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			val main = parseArgumentsCheckingReportDirectory(args)
			val executable = when {
				main.debug -> Debugger(main)
				else -> Runner(main)
			}
			executable.execute()
		}

		private fun parseArgumentsCheckingReportDirectory(args: Array<String>): Main {
			val cli = parseArguments(args)

			if (cli.reportDirectory == null && (cli.output || cli.baseline)) {
				val message = "If using --output and/or --baseline the --report path must be given!"
				failWithErrorMessage(message)
			}
			return cli
		}
	}
}
