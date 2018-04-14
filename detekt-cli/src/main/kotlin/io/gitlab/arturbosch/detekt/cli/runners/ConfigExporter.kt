package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import io.gitlab.arturbosch.detekt.cli.DEFAULT_CONFIG
import java.io.File

/**
 * @author lummax
 */
class ConfigExporter : Executable {

	override fun execute() {
		val defaultConfig = ClasspathResourceConverter().convert(DEFAULT_CONFIG).openStream()
		defaultConfig.copyTo(File(DEFAULT_CONFIG).outputStream())
		println("\nSuccessfully copied $DEFAULT_CONFIG to project location.")
	}
}
