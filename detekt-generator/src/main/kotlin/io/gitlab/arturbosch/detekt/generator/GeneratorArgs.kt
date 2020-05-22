package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.Parameter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class GeneratorArgs {

    @Parameter(names = ["--input", "-i"],
        required = true,
        description = "Input paths to analyze.")
    private var input: String? = null

    @Parameter(names = ["--documentation", "-d"],
        required = true,
        description = "Output path for generated documentation.")
    private var documentation: String? = null

    @Parameter(names = ["--config", "-c"],
        required = true,
        description = "Output path for generated detekt config.")
    private var config: String? = null

    @Parameter(names = ["--help", "-h"],
        help = true, description = "Shows the usage.")
    var help: Boolean = false

    val inputPath: List<Path> by lazy {
        checkNotNull(input) { "Input parameter was not initialized by jcommander!" }
            .splitToSequence(",", ";")
            .map(String::trim)
            .filter { it.isNotEmpty() }
            .map { first -> Paths.get(first) }
            .onEach { require(Files.exists(it)) { "Input path must exist!" } }
            .toList()
    }
    val documentationPath: Path
        get() = Paths.get(checkNotNull(documentation) {
            "Documentation output path was not initialized by jcommander!"
        })

    val configPath: Path
        get() = Paths.get(checkNotNull(config) { "Configuration output path was not initialized by jcommander!" })
}
