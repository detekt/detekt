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
	private val subConfig = config.subConfig("processors")
	private val processorsActive = subConfig.valueOrDefault("active", true)
	private val excludes = subConfig.valueOrDefault("exclude", emptyList<String>())

	fun load(): List<FileProcessListener> {
		val detektLoader = URLClassLoader(plugins, javaClass.classLoader)
		return if (processorsActive)
			ServiceLoader.load(FileProcessListener::class.java, detektLoader)
					.filter { it.id !in excludes }
					.onEach { it.init(config) }
					.toList()
		else
			emptyList()
	}
}
