package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import java.net.URL
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class FileProcessorLocator(settings: ProcessingSettings) {

	private val plugins: Array<URL> = settings.pluginUrls
	private val config: Config = settings.config

	fun load(): List<FileProcessListener> {
		val detektLoader = URLClassLoader(plugins, javaClass.classLoader)
		return ServiceLoader.load(FileProcessListener::class.java, detektLoader)
				.onEach { it.init(config) }
				.toList()
	}
}
