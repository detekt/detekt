package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

class GeneratorArgs {

    @Parameter(
        names = ["--input", "-i"],
        required = true,
        description = "Input paths to analyze."
    )
    private var input: String? = null

    @Parameter(
        names = ["--documentation", "-d"],
        required = false,
        description = "Output path for generated documentation."
    )
    private var documentation: String? = null

    @Parameter(
        names = ["--config", "-c"],
        required = false,
        description = "Output path for generated detekt config."
    )
    private var config: String? = null

    @Parameter(
        names = ["--help", "-h"],
        help = true,
        description = "Shows the usage."
    )
    var help: Boolean = false

    @Parameter(
        names = ["--generate-custom-rule-config", "-gcrc"],
        required = false,
        description = "Generate config for user-defined rules. " +
            "Path to user rules can be specified with --input option"
    )
    var generateCustomRuleConfig: Boolean = false

    @DynamicParameter(
        names = ["--replace", "-r"],
        required = false,
        description = "Any number of key and value pairs that are used to replace placeholders " +
            "during data collection and output generation. Key and value are separated by '='. " +
            "The property may be used multiple times."
    )
    var textReplacements: Map<String, String> = mutableMapOf()

    val inputPath: List<Path> by lazy {
        checkNotNull(input) { "Input parameter was not initialized by jcommander!" }
            .splitToSequence(",", ";")
            .map(String::trim)
            .filter { it.isNotEmpty() }
            .map { first -> Path(first) }
            .onEach { require(it.exists()) { "Input path must exist!" } }
            .toList()
    }
    val documentationPath: Path
        get() = Path(
            checkNotNull(documentation) {
                "Documentation output path was not initialized by jcommander!"
            }
        )

    val configPath: Path
        get() = Path(checkNotNull(config) { "Configuration output path was not initialized by jcommander!" })
}
