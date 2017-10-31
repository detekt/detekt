package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.Parameter
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Args {

	@Parameter(names = arrayOf("--input", "-i"),
			required = true,
			converter = ExistingPathConverter::class, description = "Input path to analyze (path/to/project).")
	private var input: Path? = null

	@Parameter(names = arrayOf("--filters", "-f"),
			description = "Path filters defined through regex with separator ';' (\".*test.*\").")
	var filters: String? = null // Using a converter for List<PathFilter> resulted in a ClassCastException

	@Parameter(names = arrayOf("--config", "-c"),
			description = "Path to the config file (path/to/config.yml).")
	var config: String? = null

	@Parameter(names = arrayOf("--config-resource", "-cr"),
			description = "Path to the config resource on detekt's classpath (path/to/config.yml).")
	var configResource: String? = null

	@Parameter(names = arrayOf("--generate-config", "-gc"),
			description = "Export default config to default-detekt-config.yml.")
	var generateConfig: Boolean = false

	@Parameter(names = arrayOf("--plugins", "-p"),
			description = "Extra paths to plugin jars separated by ',' or ';'.")
	var plugins: String? = null

	@Parameter(names = arrayOf("--parallel"),
			description = "Enables parallel compilation of source files." +
					" Should only be used if the analyzing project has more than ~200 kotlin files.")
	var parallel: Boolean = false

	@Parameter(names = arrayOf("--baseline", "-b"),
			description = "If a baseline xml file is passed in," +
					" only new code smells not in the baseline are printed in the console.",
			converter = PathConverter::class)
	var baseline: Path? = null

	@Parameter(names = arrayOf("--create-baseline", "-cb"),
			description = "Treats current analysis findings as a smell baseline for further detekt runs.")
	var createBaseline: Boolean = false

	@Parameter(names = arrayOf("--output", "-o"),
			description = "Directory where output reports are stored.",
			converter = PathConverter::class)
	var output: Path? = null

	@Parameter(names = arrayOf("--output-name", "-on"),
			description = "The base name for output reports is derived from this parameter.")
	var outputName: String? = null

	@Parameter(names = arrayOf("--disable-default-rulesets", "-dd"),
			description = "Disables default rule sets.")
	var disableDefaultRuleSets: Boolean = false

	@Parameter(names = arrayOf("--debug"),
			description = "Debugs given ktFile by printing its elements.")
	var debug: Boolean = false

	@Parameter(names = arrayOf("--help", "-h"),
			help = true, description = "Shows the usage.")
	var help: Boolean = false

	val inputPath: Path
		get() = input ?: throw IllegalStateException("Input path was not initialized by jcommander!")
}
