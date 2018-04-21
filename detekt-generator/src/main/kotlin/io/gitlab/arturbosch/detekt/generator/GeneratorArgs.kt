package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.Parameter
import io.gitlab.arturbosch.detekt.cli.Args
import io.gitlab.arturbosch.detekt.cli.ExistingPathConverter
import io.gitlab.arturbosch.detekt.cli.MultipleExistingPathConverter
import java.nio.file.Path

/**
 * @author Marvin Ramin
 * @author Artur Bosch
 */
class GeneratorArgs : Args {

	@Parameter(names = ["--input", "-i"],
			required = true,
			description = "Input paths to analyze.")
	private var input: String? = null

	@Parameter(names = ["--documentation", "-d"],
			required = true,
			converter = ExistingPathConverter::class, description = "Output path for generated documentation.")
	private var documentation: Path? = null

	@Parameter(names = ["--config", "-c"],
			required = true,
			converter = ExistingPathConverter::class, description = "Output path for generated detekt config.")
	private var config: Path? = null

	@Parameter(names = ["--help", "-h"],
			help = true, description = "Shows the usage.")
	override var help: Boolean = false

	val inputPath: List<Path> by lazy {
		MultipleExistingPathConverter().convert(input
				?: throw IllegalStateException("Input parameter was not initialized by jcommander!"))
	}
	val documentationPath: Path
		get() = documentation
				?: throw IllegalStateException("Documentation output path was not initialized by jcommander!")

	val configPath: Path
		get() = config ?: throw IllegalStateException("Configuration output path was not initialized by jcommander!")
}
