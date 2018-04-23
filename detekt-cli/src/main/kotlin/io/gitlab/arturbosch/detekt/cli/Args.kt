package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.Parameter
import java.nio.file.Path

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class Args {

	@Parameter(names = ["--input", "-i"],
			required = true,
			description = "Input paths to analyze.")
	private var input: String? = null

	@Parameter(names = ["--filters", "-f"],
			description = "Path filters defined through regex with separator ';' (\".*test.*\").")
	var filters: String? = null // Using a converter for List<PathFilter> resulted in a ClassCastException

	@Parameter(names = ["--config", "-c"],
			description = "Path to the config file (path/to/config.yml).")
	var config: String? = null

	@Parameter(names = ["--config-resource", "-cr"],
			description = "Path to the config resource on detekt's classpath (path/to/config.yml).")
	var configResource: String? = null

	@Parameter(names = ["--generate-config", "-gc"],
			description = "Export default config to default-detekt-config.yml.")
	var generateConfig: Boolean = false

	@Parameter(names = ["--plugins", "-p"],
			description = "Extra paths to plugin jars separated by ',' or ';'.")
	var plugins: String? = null

	@Parameter(names = ["--parallel"],
			description = "Enables parallel compilation of source files." +
					" Should only be used if the analyzing project has more than ~200 kotlin files.")
	var parallel: Boolean = false

	@Parameter(names = ["--baseline", "-b"],
			description = "If a baseline xml file is passed in," +
					" only new code smells not in the baseline are printed in the console.",
			converter = PathConverter::class)
	var baseline: Path? = null

	@Parameter(names = ["--create-baseline", "-cb"],
			description = "Treats current analysis findings as a smell baseline for future detekt runs.")
	var createBaseline: Boolean = false

	@Parameter(names = ["--report-xml", "-rx"],
			description = "The path the XML report output should be stored in. If this argument is not supplied no XML" +
					" report will be written.")
	var xmlReport: String? = null

	@Parameter(names = ["--report-html", "-rh"],
			description = "The path the HTML report output should be stored in. If this argument is not supplied no" +
					" HTML report will be written")
	var htmlReport: String? = null

	@Parameter(names = ["--report-plain", "-rp"],
			description = "The path the plain (.txt) report output should be stored in. If this argument is not supplied no" +
					" plain report will be written")
	var plainReport: String? = null

	@Parameter(names = ["--disable-default-rulesets", "-dd"],
			description = "Disables default rule sets.")
	var disableDefaultRuleSets: Boolean = false

	@Parameter(names = ["--debug"],
			description = "Prints extra information about configurations and extensions.")
	var debug: Boolean = false

	@Parameter(names = ["--help", "-h"],
			help = true, description = "Shows the usage.")
	var help: Boolean = false

	@Parameter(names = ["--run-rule"],
			description = "Specify a rule by [RuleSet:Rule] pattern and run it on input.")
	var runRule: String? = null

	@Parameter(names = ["--print-ast"],
			description = "Prints the AST for given [input] file. Must be no directory.")
	var printAst: Boolean = false

	val inputPath: List<Path> by lazy {
		MultipleExistingPathConverter().convert(input
				?: throw IllegalStateException("Input parameter was not initialized by jcommander!"))
	}
}
