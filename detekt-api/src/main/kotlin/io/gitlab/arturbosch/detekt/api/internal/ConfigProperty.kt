package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.ConfigAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun config(defaultValue: String): ReadOnlyProperty<ConfigAware, String> = simpleConfig(defaultValue)
fun config(defaultValue: Int): ReadOnlyProperty<ConfigAware, Int> = simpleConfig(defaultValue)
fun config(defaultValue: Long): ReadOnlyProperty<ConfigAware, Long> = simpleConfig(defaultValue)
fun config(defaultValue: Boolean): ReadOnlyProperty<ConfigAware, Boolean> = simpleConfig(defaultValue)
fun config(defaultValue: List<String>): ReadOnlyProperty<ConfigAware, List<String>> = ListConfigProperty(defaultValue)

private fun <T : Any> simpleConfig(defaultValue: T): ReadOnlyProperty<ConfigAware, T> =
    SimpleConfigProperty(defaultValue)

private class SimpleConfigProperty<T : Any>(private val defaultValue: T) : ReadOnlyProperty<ConfigAware, T> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): T {
        return thisRef.valueOrDefault(property.name, defaultValue)
    }
}

private class ListConfigProperty(private val defaultValue: List<String>) : ReadOnlyProperty<ConfigAware, List<String>> {
    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): List<String> {
        return thisRef.valueOrDefaultCommaSeparated(property.name, defaultValue)
    }
}
