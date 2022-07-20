package io.gitlab.arturbosch.detekt.core.config.validation

import io.github.detekt.utils.openSafeStream
import java.util.Properties

internal fun loadDeprecations(): Map<String, String> {
    return ValidationSettings::class.java.classLoader
        .getResource("deprecation.properties")!!
        .openSafeStream()
        .use { inputStream ->
            val prop = Properties().apply { load(inputStream) }

            prop.entries.associate { entry ->
                (entry.key as String) to (entry.value as String)
            }
        }
}
