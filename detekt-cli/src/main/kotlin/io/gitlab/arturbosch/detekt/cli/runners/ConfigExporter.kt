package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.gitlab.arturbosch.detekt.cli.CliArgs
import java.io.PrintStream
import java.nio.file.Paths

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: PrintStream,
) : Executable {

    override fun execute() {
        val configPath = Paths.get(arguments.config ?: "detekt.yaml")
        DefaultConfigurationProvider.load().copy(configPath)
        outputPrinter.println("Successfully copied default config to ${configPath.toAbsolutePath()}")
    }
}
