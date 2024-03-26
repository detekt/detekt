package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.IValueValidator
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import com.beust.jcommander.converters.IParameterSplitter
import com.beust.jcommander.converters.PathConverter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

class GeneratorArgs {

    @Parameter(
        names = ["--input", "-i"],
        required = true,
        converter = PathConverter::class,
        splitter = PathSplitter::class,
        validateValueWith = [PathValidator::class],
        description = "Input paths to analyze."
    )
    var inputPath: List<Path> = emptyList()

    @Parameter(
        names = ["--documentation", "-d"],
        required = true,
        converter = PathConverter::class,
        description = "Output path for generated documentation."
    )
    var documentationPath: Path = Path("")

    @Parameter(
        names = ["--config", "-c"],
        required = true,
        converter = PathConverter::class,
        description = "Output path for generated detekt config."
    )
    var configPath: Path = Path("")

    @Parameter(
        names = ["--help", "-h"],
        help = true,
        description = "Shows the usage."
    )
    var help: Boolean = false

    @Parameter(
        names = ["--generate-custom-rule-config", "-gcrc"],
        description = "Generate config for user-defined rules. " +
            "Path to user rules can be specified with --input option"
    )
    var generateCustomRuleConfig: Boolean = false

    @DynamicParameter(
        names = ["--replace", "-r"],
        description = "Any number of key and value pairs that are used to replace placeholders " +
            "during data collection and output generation. Key and value are separated by '='. " +
            "The property may be used multiple times."
    )
    var textReplacements: Map<String, String> = mutableMapOf()

    class PathSplitter : IParameterSplitter {
        override fun split(value: String): List<String> {
            return value.split(',', ';')
        }
    }

    class PathValidator : IValueValidator<List<Path>> {
        override fun validate(name: String, value: List<Path>) {
            value.forEach {
                if (!it.exists()) throw ParameterException("Input path does not exist: $it")
            }
        }
    }
}
