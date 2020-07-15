package io.gitlab.arturbosch.detekt.core.extensions

import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.core.NL
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.util.ServiceLoader

val LIST_ITEM_SPACING = "$NL    "

inline fun <reified T : Extension> loadExtensions(
    settings: ProcessingSettings,
    predicate: (T) -> Boolean = { true }
): List<T> =
    ServiceLoader.load(T::class.java, settings.pluginLoader)
        .filterNot { it.id in settings.spec.extensionsSpec.disabledExtensions }
        .filter(predicate)
        .sortedBy { it.priority }
        .asReversed()
        .onEach { it.init(settings.config); it.init(settings) }
        .also { settings.debug { "Loaded extensions: $LIST_ITEM_SPACING${it.joinToString(LIST_ITEM_SPACING)}" } }
