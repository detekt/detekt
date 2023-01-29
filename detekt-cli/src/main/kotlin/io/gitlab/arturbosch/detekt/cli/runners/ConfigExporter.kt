package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.MultipleExistingPathConverter
import kotlin.io.path.Path

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: Appendable,
) : Executable {

    override fun execute() {
        val configPath = Path(arguments.config ?: "detekt.yml")
        val spec = ProcessingSpec {
            extensions {
                disableDefaultRuleSets = arguments.disableDefaultRuleSets
                fromPaths { arguments.plugins?.let { MultipleExistingPathConverter().convert(it) }.orEmpty() }
            }
        }
        DefaultConfigurationProvider.load(spec.extensionsSpec).copy(configPath)
        outputPrinter.appendLine("Successfully copied default config to ${configPath.toAbsolutePath()}")
    }
}
