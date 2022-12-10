package io.github.detekt.compiler.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.nio.file.Paths
import java.util.Base64

@OptIn(ExperimentalCompilerApi::class)
class DetektCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "detekt-compiler-plugin"

    @Suppress("StringLiteralDuplication")
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            Options.config,
            "<path|paths>",
            "Comma separated paths to detekt config files.",
            false
        ),
        CliOption(
            Options.configDigest,
            "<digest>",
            "A digest calculated from the content of the config files. Used for Gradle incremental task invalidation.",
            false
        ),
        CliOption(
            Options.baseline,
            "<path>",
            "Path to a detekt baseline file.",
            false
        ),
        CliOption(
            Options.debug,
            "<true|false>",
            "Print debug messages.",
            false
        ),
        CliOption(
            Options.isEnabled,
            "<true|false>",
            "Should detekt run?",
            false
        ),
        CliOption(
            Options.useDefaultConfig,
            "<true|false>",
            "Use the default detekt config as baseline.",
            false
        ),
        CliOption(
            Options.allRules,
            "<true|false>",
            "Turns on all the rules.",
            false
        ),
        CliOption(
            Options.disableDefaultRuleSets,
            "<true|false>",
            "Disables all default detekt rulesets and will only run detekt with custom rules " +
                "defined in plugins passed in with `detektPlugins` configuration.",
            false
        ),
        CliOption(
            Options.parallel,
            "<true|false>",
            "Enables parallel compilation and analysis of source files.",
            false
        ),
        CliOption(
            Options.rootPath,
            "<path>",
            "Root path used to relativize paths when using exclude patterns.",
            false
        ),
        CliOption(
            Options.excludes,
            "<base64-encoded globs>",
            "A base64-encoded list of the globs used to exclude paths from scanning.",
            false
        ),
        CliOption(
            Options.report,
            "<report-id:path>",
            "Generates a report for given 'report-id' and stores it on given 'path'. " +
                "Available 'report-id' values: 'txt', 'xml', 'html'.",
            false,
            allowMultipleOccurrences = true
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            Options.baseline -> configuration.put(Keys.BASELINE, Paths.get(value))
            Options.config -> configuration.put(Keys.CONFIG, value.split(",;").map { Paths.get(it) })
            Options.configDigest -> configuration.put(Keys.CONFIG_DIGEST, value)
            Options.debug -> configuration.put(Keys.DEBUG, value.toBoolean())
            Options.isEnabled -> configuration.put(Keys.IS_ENABLED, value.toBoolean())
            Options.useDefaultConfig -> configuration.put(Keys.USE_DEFAULT_CONFIG, value.toBoolean())
            Options.allRules -> configuration.put(Keys.ALL_RULES, value.toBoolean())
            Options.disableDefaultRuleSets -> configuration.put(Keys.DISABLE_DEFAULT_RULE_SETS, value.toBoolean())
            Options.parallel -> configuration.put(Keys.PARALLEL, value.toBoolean())
            Options.rootPath -> configuration.put(Keys.ROOT_PATH, Paths.get(value))
            Options.excludes -> configuration.put(Keys.EXCLUDES, value.decodeToGlobSet())
            Options.report -> configuration.put(
                Keys.REPORTS,
                value.substringBefore(':'),
                Paths.get(value.substringAfter(':')),
            )
            else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
        }
    }
}

private fun String.decodeToGlobSet(): List<String> {
    val b = Base64.getDecoder().decode(this)
    val bi = ByteArrayInputStream(b)

    return ObjectInputStream(bi).use { inputStream ->
        val globs = mutableListOf<String>()

        repeat(inputStream.readInt()) {
            globs.add(inputStream.readUTF())
        }

        globs
    }
}
