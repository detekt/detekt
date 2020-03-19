package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.DEFAULT_CONFIG
import java.io.File

class ConfigExporter(private val arguments: CliArgs) : Executable {

    override fun execute() {
        val configPath = arguments.config ?: DEFAULT_CONFIG
        val defaultConfig = ClasspathResourceConverter().convert(DEFAULT_CONFIG).openStream()
        defaultConfig.copyTo(File(configPath).outputStream())
        println("Successfully copied default config to ${File(configPath).absolutePath}")
    }
}
