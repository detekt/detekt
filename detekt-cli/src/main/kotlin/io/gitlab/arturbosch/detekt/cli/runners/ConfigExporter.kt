package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.createSpec
import java.nio.file.Paths

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: Appendable,
    private val errorPrinter: Appendable,
) : Executable {

    override fun execute() {
        val configPath = Paths.get(arguments.config ?: "detekt.yml")
        val spec = arguments.createSpec(outputPrinter, errorPrinter)
        DefaultConfigurationProvider.load(spec).copy(configPath)
        outputPrinter.appendLine("Successfully copied default config to ${configPath.toAbsolutePath()}")
    }
}
