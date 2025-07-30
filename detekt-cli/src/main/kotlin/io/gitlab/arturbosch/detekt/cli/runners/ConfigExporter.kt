package io.gitlab.arturbosch.detekt.cli.runners

import dev.detekt.tooling.api.DefaultConfigurationProvider
import dev.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.cli.CliArgs
import kotlin.io.path.absolute

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: Appendable,
) : Executable {

    override fun execute() {
        val configPath = arguments.generateConfig ?: error("Unexpected error generating config file")
        val spec = ProcessingSpec {
            extensions {
                fromPaths { arguments.plugins }
            }
        }
        DefaultConfigurationProvider.load(spec.extensionsSpec).copy(configPath)
        outputPrinter.appendLine("Successfully copied default config to ${configPath.absolute()}")
    }
}
