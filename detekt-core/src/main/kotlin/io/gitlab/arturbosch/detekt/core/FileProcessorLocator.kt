package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import java.util.ServiceLoader

class FileProcessorLocator(private val settings: ProcessingSettings) {

    private val config: Config = settings.config
    private val subConfig = config.subConfig("processors")
    private val processorsActive = subConfig.valueOrDefault("active", true)
    private val excludes = subConfig.valueOrDefault("exclude", emptyList<String>())

    fun load(): List<FileProcessListener> =
        if (processorsActive) {
            ServiceLoader.load(FileProcessListener::class.java, settings.pluginLoader)
                .filter { it.id !in excludes }
                .onEach { it.init(config); it.init(settings) }
                .toList()
                .also { settings.debug { "Registered file processors: $it" } }
        } else {
            emptyList()
        }
}
