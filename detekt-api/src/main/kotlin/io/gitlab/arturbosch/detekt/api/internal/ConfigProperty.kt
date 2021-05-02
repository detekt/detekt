package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.ConfigAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> config(defaultValue: T): ReadOnlyProperty<ConfigAware, T> =
    SimpleConfigProperty(defaultValue)

fun <T : Any> configWithFallback(fallbackPropertyName: String, defaultValue: T): ReadOnlyProperty<ConfigAware, T> =
    FallbackConfigProperty(fallbackPropertyName, defaultValue)

fun configList(defaultValue: List<String>): ReadOnlyProperty<ConfigAware, List<String>> =
    ListConfigProperty(defaultValue)

private class SimpleConfigProperty<T : Any>(private val defaultValue: T) : ReadOnlyProperty<ConfigAware, T> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): T {
        return thisRef.valueOrDefault(property.name, defaultValue)
    }
}

private class FallbackConfigProperty<T : Any>(
    private val fallbackPropertyName: String,
    private val defaultValue: T
) : ReadOnlyProperty<ConfigAware, T> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): T {
        return thisRef.valueOrDefault(property.name, thisRef.valueOrDefault(fallbackPropertyName, defaultValue))
    }
}

private class ListConfigProperty(private val defaultValue: List<String>) : ReadOnlyProperty<ConfigAware, List<String>> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): List<String> {
        return thisRef.valueOrDefaultCommaSeparated(property.name, defaultValue)
    }
}
