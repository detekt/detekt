package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.ConfigAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> config(defaultValue: T): ReadOnlyProperty<ConfigAware, T> =
    SimpleConfigProperty(defaultValue)

fun <T : Any> configWithFallback(fallbackPropertyName: String, defaultValue: T): ReadOnlyProperty<ConfigAware, T> =
    FallbackConfigProperty(fallbackPropertyName, defaultValue)

private fun <T : Any> getValueOrDefault(configAware: ConfigAware, propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (defaultValue) {
        is List<*> -> {
            if (defaultValue.all { it is String }) {
                val defaultValueAsListOfStrings = defaultValue as List<String>
                configAware.valueOrDefaultCommaSeparated(propertyName, defaultValueAsListOfStrings) as T
            } else {
                error("Only lists of strings are supported. '$propertyName' is invalid. ")
            }
        }
        is String,
        is Boolean,
        is Int,
        is Long -> configAware.valueOrDefault(propertyName, defaultValue)
        else -> error(
            "${defaultValue.javaClass} is not supported for delegated config property '$propertyName'. " +
                "Use one of String, Boolean, Int, Long or List<String> instead."
        )
    }
}

private class SimpleConfigProperty<T : Any>(private val defaultValue: T) : ReadOnlyProperty<ConfigAware, T> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): T {
        return getValueOrDefault(thisRef, property.name, defaultValue)
    }
}

private class FallbackConfigProperty<T : Any>(
    private val fallbackPropertyName: String,
    private val defaultValue: T
) : ReadOnlyProperty<ConfigAware, T> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): T {
        return getValueOrDefault(thisRef, property.name, getValueOrDefault(thisRef, fallbackPropertyName, defaultValue))
    }
}
