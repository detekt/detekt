package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import java.net.URL
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class FileProcessorLocator(private val plugins: Array<URL>,
						   private val config: Config) {

	companion object {
		fun instance(settings: ProcessingSettings) = with(settings) {
			FileProcessorLocator(settings.pluginUrls, settings.config)
		}
	}

	fun load(): List<FileProcessListener> {
		val detektLoader = URLClassLoader(plugins, javaClass.classLoader)
		return ServiceLoader.load(FileProcessListener::class.java, detektLoader)
				.onEach { it.init(config) }
				.toList()
	}
}
