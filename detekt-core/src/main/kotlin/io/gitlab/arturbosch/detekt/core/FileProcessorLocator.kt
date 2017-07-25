package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import java.net.URL
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class FileProcessorLocator(private val plugins: Array<URL>) {

	companion object {
		fun instance(settings: ProcessingSettings) = with(settings) {
			FileProcessorLocator(settings.pluginUrls)
		}
	}

	fun load(): List<FileProcessListener> {
		val detektLoader = URLClassLoader(plugins, javaClass.classLoader)
		return ServiceLoader.load(FileProcessListener::class.java, detektLoader).toList()
	}
}
