package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.gitlab.arturbosch.detekt.cli.CliArgs
import java.nio.file.Paths

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: Appendable
) : Executable {

    override fun execute() {
        val configPath = Paths.get(arguments.config ?: "detekt.yml")
        DefaultConfigurationProvider.load().copy(configPath)
        outputPrinter.appendLine("Successfully copied default config to ${configPath.toAbsolutePath()}")
    }
}
