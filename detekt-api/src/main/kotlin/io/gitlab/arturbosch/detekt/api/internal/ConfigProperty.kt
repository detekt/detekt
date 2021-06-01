package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.ConfigAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> config(
    defaultValue: T
): ReadOnlyProperty<ConfigAware, T> = config(defaultValue) { it }

fun <T : Any, U : Any> config(
    defaultValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> = TransformedConfigProperty(defaultValue, transformer)

fun <T : Any> configWithFallback(
    fallbackPropertyName: String,
    defaultValue: T
): ReadOnlyProperty<ConfigAware, T> = configWithFallback(fallbackPropertyName, defaultValue) { it }

fun <T : Any, U : Any> configWithFallback(
    fallbackPropertyName: String,
    defaultValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> = FallbackConfigProperty(fallbackPropertyName, defaultValue, transformer)

fun <T : Any> configWithAndroidVariants(
    defaultValue: T,
    defaultAndroidValue: T,
): ReadOnlyProperty<ConfigAware, T> = configWithAndroidVariants(defaultValue, defaultAndroidValue) { it }

fun <T : Any, U : Any> configWithAndroidVariants(
    defaultValue: T,
    defaultAndroidValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> =
    TransformedConfigPropertyWithAndroidVariants(defaultValue, defaultAndroidValue, transformer)

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
        is Int -> configAware.valueOrDefault(propertyName, defaultValue)
        else -> error(
            "${defaultValue.javaClass} is not supported for delegated config property '$propertyName'. " +
                "Use one of String, Boolean, Int or List<String> instead."
        )
    }
}

private abstract class MemoizedConfigProperty<U : Any> : ReadOnlyProperty<ConfigAware, U> {
    private var value: U? = null

    override fun getValue(thisRef: ConfigAware, property: KProperty<*>): U {
        return value ?: doGetValue(thisRef, property).also { value = it }
    }

    abstract fun doGetValue(thisRef: ConfigAware, property: KProperty<*>): U
}

private class TransformedConfigPropertyWithAndroidVariants<T : Any, U : Any>(
    private val defaultValue: T,
    private val defaultAndroidValue: T,
    private val transform: (T) -> U
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: ConfigAware, property: KProperty<*>): U {
        val isAndroid = getValueOrDefault(thisRef, "android", false)
        val value = if (isAndroid) defaultAndroidValue else defaultValue
        return transform(getValueOrDefault(thisRef, property.name, value))
    }
}

private class TransformedConfigProperty<T : Any, U : Any>(
    private val defaultValue: T,
    private val transform: (T) -> U
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: ConfigAware, property: KProperty<*>): U {
        return transform(getValueOrDefault(thisRef, property.name, defaultValue))
    }
}

private class FallbackConfigProperty<T : Any, U : Any>(
    private val fallbackPropertyName: String,
    private val defaultValue: T,
    private val transform: (T) -> U
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: ConfigAware, property: KProperty<*>): U {
        val fallbackValue = getValueOrDefault(thisRef, fallbackPropertyName, defaultValue)
        return transform(getValueOrDefault(thisRef, property.name, fallbackValue))
    }
}
