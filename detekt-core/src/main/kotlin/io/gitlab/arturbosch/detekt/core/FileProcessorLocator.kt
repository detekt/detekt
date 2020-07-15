package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.core.extensions.loadExtensions

class FileProcessorLocator(private val settings: ProcessingSettings) {

    private val config: Config = settings.config
    private val subConfig = config.subConfig("processors")
    private val processorsActive = subConfig.valueOrDefault("active", true)
    private val excludes = subConfig.valueOrDefault("exclude", emptyList<String>())

    fun load(): List<FileProcessListener> {
        var processors: List<FileProcessListener> = if (processorsActive) {
            loadExtensions(settings) { it.id !in excludes }
        } else {
            emptyList()
        }
        if (settings.spec.rulesSpec.autoCorrect) {
            val modifier = KtFileModifier()
            if (modifier.id !in excludes) {
                processors = processors + modifier
            }
        }
        return processors
    }
}
