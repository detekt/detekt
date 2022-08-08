package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.MultipleExistingPathConverter
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Paths

class ConfigExporter(
    private val arguments: CliArgs,
    private val outputPrinter: Appendable,
) : Executable {

    override fun execute() {
        if (arguments.generateCustomRuleConfig) {
            generateCustomRuleConfig()
        } else {
            generateConfig()
        }
    }

    private fun generateConfig() {
        val configPath = Paths.get(arguments.config ?: "detekt.yml")
        val spec = ProcessingSpec {
            extensions {
                disableDefaultRuleSets = arguments.disableDefaultRuleSets
                fromPaths { arguments.plugins?.let { MultipleExistingPathConverter().convert(it) }.orEmpty() }
            }
        }
        DefaultConfigurationProvider.load(spec.extensionsSpec).copy(configPath)
        outputPrinter.appendLine("Successfully copied default config to ${configPath.toAbsolutePath()}")
    }

    private fun generateCustomRuleConfig() {
        @Suppress("UnsafeCallOnNullableType")
        val rootDir = File(arguments.classpath!!)
        val urls = rootDir.walkTopDown()
            .filter { it.name.endsWith(".jar") }
            .map { it.toURI().toURL() }
            .toList()
            .toTypedArray()
        val classLoader = URLClassLoader(urls, null)

        val clazz = classLoader.loadClass("io.gitlab.arturbosch.detekt.generator.Main")
        val methodMain = clazz.getMethod("main", Array<String>::class.java)

        val args = arrayOf(
            arguments::generateCustomRuleConfig.name.toParam(),
            arguments::input.name.toParam(),
            arguments.input,
        )
        methodMain.invoke(null, args)
        outputPrinter.appendLine("Successfully generated custom rules config to /resources/config/")
    }

    private fun String.toParam() =
        @Suppress("UnsafeCallOnNullableType")
        arguments.javaClass.declaredFields.find { it.name == this }!!
            .getAnnotation(com.beust.jcommander.Parameter::class.java)
            .names
            .first()
}
