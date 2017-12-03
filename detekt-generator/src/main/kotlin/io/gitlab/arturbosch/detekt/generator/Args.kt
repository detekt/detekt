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

	@Parameter(names = arrayOf("--input", "-i"),
			required = true,
			converter = ExistingPathConverter::class, description = "Input path to analyze (path/to/project).")
	private var input: Path? = null

	@Parameter(names = arrayOf("--output", "-o"),
			required = true,
			converter = ExistingPathConverter::class, description = "Output path for generated documentation.")
	private var output: Path? = null

	@Parameter(names = arrayOf("--help", "-h"),
			help = true, description = "Shows the usage.")
	var help: Boolean = false

	val inputPath: Path
		get() = input ?: throw IllegalStateException("Input path was not initialized by jcommander!")

	val outputPath: Path
		get() = output ?: throw IllegalStateException("Output path was not initialized by jcommander!")
}

class ExistingPathConverter : IStringConverter<Path> {
	override fun convert(value: String): Path {
		val config = File(value).toPath()
		if (Files.notExists(config))
			throw ParameterException("Provided path '$value' does not exist!")
		return config
	}
}
