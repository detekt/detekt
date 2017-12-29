package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class Args {

	@Parameter(names = ["--input", "-i"],
			required = true,
			converter = ExistingPathConverter::class, description = "Input path to analyze (path/to/project).")
	private var input: Path? = null

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
	var help: Boolean = false

	val inputPath: Path
		get() = input ?: throw IllegalStateException("Input path was not initialized by jcommander!")

	val documentationPath: Path
		get() = documentation ?: throw IllegalStateException("Documentation output path was not initialized by jcommander!")

	val configPath: Path
		get() = config ?: throw IllegalStateException("Configuration output path was not initialized by jcommander!")
}

class ExistingPathConverter : IStringConverter<Path> {
	override fun convert(value: String): Path {
		val config = File(value).toPath()
		if (Files.notExists(config))
			throw ParameterException("Provided path '$value' does not exist!")
		return config
	}
}
