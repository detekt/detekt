package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.Parameter
import java.nio.file.Path

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
interface Args {
	var help: Boolean
}

class CliArgs : Args {

	@Parameter(names = ["--input", "-i"],
			description = "Input paths to analyze. Multiple paths are separated by comma. If not specified the " +
					"current working directory is used.")
	private var input: String? = null

	@Parameter(names = ["--filters", "-f"],
			description = "Path filters defined through regex with separator ';' or ',' (\".*test.*\"). " +
					"These filters apply on relative paths from the project root.")
	var filters: String? = null // Using a converter for List<PathFilter> resulted in a ClassCastException

	@Parameter(names = ["--config", "-c"],
			description = "Path to the config file (path/to/config.yml). " +
					"Multiple configuration files can be specified with ',' or ';' as separator.")
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

	@Parameter(names = ["--report", "-r"],
			description = "Generates a report for given 'report-id' and stores it on given 'path'. " +
					"Entry should consist of: [report-id:path]. " +
					"Available 'report-id' values: 'txt', 'xml', 'html'. " +
					"These can also be used in combination with each other " +
					"e.g. '-r txt:reports/detekt.txt -r xml:reports/detekt.xml'")
	private var reports: List<String>? = null

	@Parameter(names = ["--disable-default-rulesets", "-dd"],
			description = "Disables default rule sets.")
	var disableDefaultRuleSets: Boolean = false

	@Parameter(names = ["--build-upon-default-config"],
			description = "Uses the default detekt configuration as a baseline. " +
					"Allows additional provided configurations to override the defaults.")
	var buildUponDefaultConfig: Boolean = false

	@Parameter(names = ["--fail-fast"],
			description = "Uses the default detekt configuration as a baseline but sets all rules to active! " +
					"Additional configuration files can override properties except the 'active' one.")
	var failFast: Boolean = false

	@Parameter(names = ["--debug"],
			description = "Prints extra information about configurations and extensions.")
	var debug: Boolean = false

	@Parameter(names = ["--help", "-h"],
			help = true, description = "Shows the usage.")
	override var help: Boolean = false

	@Parameter(names = ["--run-rule"],
			description = "Specify a rule by [RuleSet:Rule] pattern and run it on input.",
			hidden = true)
	var runRule: String? = null

	@Parameter(names = ["--print-ast"],
			description = "Prints the AST for given [input] file. Must be no directory.",
			hidden = true)
	var printAst: Boolean = false

	val inputPaths: List<Path> by lazy {
		MultipleExistingPathConverter().convert(input ?: System.getProperty("user.dir"))
	}

	val reportPaths: List<ReportPath> by lazy {
		reports?.map { ReportPath.from(it) } ?: emptyList()
	}
}
