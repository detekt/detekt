package io.github.detekt.compiler.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.Base64
import kotlin.io.path.Path

class DetektCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "detekt-compiler-plugin"

    @Suppress("StringLiteralDuplication")
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            Options.CONFIG,
            "<path|paths>",
            "Comma separated paths to detekt config files.",
            false,
            allowMultipleOccurrences = true,
        ),
        CliOption(
            Options.CONFIG_DIGEST,
            "<digest>",
            "A digest calculated from the content of the config files. Used for Gradle incremental task invalidation.",
            false
        ),
        CliOption(
            Options.BASELINE,
            "<path>",
            "Path to a detekt baseline file.",
            false
        ),
        CliOption(
            Options.DEBUG,
            "<true|false>",
            "Print debug messages.",
            false
        ),
        CliOption(
            Options.IS_ENABLED,
            "<true|false>",
            "Should detekt run?",
            false
        ),
        CliOption(
            Options.USE_DEFAULT_CONFIG,
            "<true|false>",
            "Use the default detekt config as baseline.",
            false
        ),
        CliOption(
            Options.ALL_RULES,
            "<true|false>",
            "Turns on all the rules.",
            false
        ),
        CliOption(
            Options.DISABLE_DEFAULT_RULE_SETS,
            "<true|false>",
            "Disables all default detekt rulesets and will only run detekt with custom rules " +
                "defined in plugins passed in with `detektPlugins` configuration.",
            false
        ),
        CliOption(
            Options.PARALLEL,
            "<true|false>",
            "Enables parallel compilation and analysis of source files.",
            false
        ),
        CliOption(
            Options.ROOT_PATH,
            "<path>",
            "Root path used to relativize paths when using exclude patterns.",
            false
        ),
        CliOption(
            Options.EXCLUDES,
            "<base64-encoded globs>",
            "A base64-encoded list of the globs used to exclude paths from scanning.",
            false
        ),
        CliOption(
            Options.REPORT,
            "<report-id:path>",
            "Generates a report for given 'report-id' and stores it on given 'path'. " +
                "Available 'report-id' values: 'xml', 'html'.",
            false,
            allowMultipleOccurrences = true
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            Options.BASELINE -> configuration.put(Keys.BASELINE, Path(value))
            Options.CONFIG -> configuration.appendList(Keys.CONFIG, Path(value))
            Options.CONFIG_DIGEST -> configuration.put(Keys.CONFIG_DIGEST, value)
            Options.DEBUG -> configuration.put(Keys.DEBUG, value.toBoolean())
            Options.IS_ENABLED -> configuration.put(Keys.IS_ENABLED, value.toBoolean())
            Options.USE_DEFAULT_CONFIG -> configuration.put(Keys.USE_DEFAULT_CONFIG, value.toBoolean())
            Options.ALL_RULES -> configuration.put(Keys.ALL_RULES, value.toBoolean())
            Options.DISABLE_DEFAULT_RULE_SETS -> configuration.put(Keys.DISABLE_DEFAULT_RULE_SETS, value.toBoolean())
            Options.PARALLEL -> configuration.put(Keys.PARALLEL, value.toBoolean())
            Options.ROOT_PATH -> configuration.put(Keys.ROOT_PATH, Path(value))
            Options.EXCLUDES -> configuration.put(Keys.EXCLUDES, value.decodeToGlobSet())
            Options.REPORT -> configuration.put(
                Keys.REPORTS,
                value.substringBefore(':'),
                Path(value.substringAfter(':')),
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
