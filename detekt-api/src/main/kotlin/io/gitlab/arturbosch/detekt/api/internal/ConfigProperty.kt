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
        is List<*> -> configAware.valueOrDefaultCommaSeparated(propertyName, defaultValue as List<String>) as T
        is String,
        is Boolean,
        is Int,
        is Long -> configAware.valueOrDefault(propertyName, defaultValue)
        else -> error("${defaultValue.javaClass} is not supported as ")
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
