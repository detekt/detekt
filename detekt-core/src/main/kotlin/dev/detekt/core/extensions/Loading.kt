package dev.detekt.core.extensions

import dev.detekt.api.Extension
import dev.detekt.core.NL
import dev.detekt.core.ProcessingSettings
import java.util.ServiceLoader

val LIST_ITEM_SPACING = "$NL    "

inline fun <reified T : Extension> loadExtensions(
    settings: ProcessingSettings,
    predicate: (T) -> Boolean = { true },
): List<T> =
    ServiceLoader.load(T::class.java, settings.pluginLoader)
        .filterNot { it.id in settings.spec.extensionsSpec.disabledExtensions }
        .filter(predicate)
        .sortedByDescending { it.priority }
        .onEach {
            it.init(settings)
        }
        .also {
            settings.debug {
                "Loaded extensions: $LIST_ITEM_SPACING" +
                    it.joinToString(LIST_ITEM_SPACING) { it.javaClass.canonicalName }
            }
        }
