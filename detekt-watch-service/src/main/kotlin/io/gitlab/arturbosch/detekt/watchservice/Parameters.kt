package io.gitlab.arturbosch.detekt.watchservice

import com.beust.jcommander.Parameter
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.ExistingPathConverter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class Parameters {

	@Parameter(names = arrayOf("--input", "-i"),
			converter = ExistingPathConverter::class,
			description = "Input path to analyze (path/to/project).")
	private var input: Path? = null

	@Parameter(names = arrayOf("--config", "-c"),
			description = "Path to the config file (path/to/config.yml).")
	private var config: String? = null

	fun extractWatchDirectory(): Path {
		return input?.apply {
			require(Files.isDirectory(input)) {
				"Make sure that given path must exist and be a directory with kotlin files to watch."
			}
		} ?: Paths.get(".")

	}

	fun extractConfig(): Config =
			config?.let { YamlConfig.load(ExistingPathConverter().convert(it)) } ?: Config.empty
}
