package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 */
fun <T : Any> config(
    defaultValue: T
): ReadOnlyProperty<ConfigAware, T> = config(defaultValue) { it }

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated and transformed only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 * @param transformer a function that transforms the value from the configuration (or the default) into its final
 * value. A typical use case for this is a conversion from a [String] into a [Regex].
 */
fun <T : Any, U : Any> config(
    defaultValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> = TransformedConfigProperty(defaultValue, transformer)

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. If there is no such property, the value of the
 * supplied [fallbackProperty] is also considered before using the [defaultValue].
 * The value of the property is evaluated only once.
 *
 * This method is only intended to be used in migration scenarios where there is no way to update all configuration
 * files immediately.
 *
 * @param fallbackProperty The reference to the configuration key to fall back to. This property must be defined as a
 * configuration delegate.
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 */
@UnstableApi("fallback property handling is still under discussion")
fun <T : Any> configWithFallback(
    fallbackProperty: KProperty0<T>,
    defaultValue: T
): ReadOnlyProperty<ConfigAware, T> = configWithFallback(fallbackProperty, defaultValue) { it }

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. If there is no such property, the value of the
 * supplied [fallbackProperty] is also considered before using the [defaultValue].
 * The value of the property is evaluated and transformed only once.
 *
 * This method is only intended to be used in migration scenarios where there is no way to update all configuration
 * files immediately.
 *
 * @param fallbackProperty The reference to the configuration key to fall back to. This property must be defined as a
 * configuration delegate.
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 * @param transformer a function that transforms the value from the configuration (or the default) into its final
 * value.
 */
@UnstableApi("fallback property handling is still under discussion")
fun <T : Any, U : Any> configWithFallback(
    fallbackProperty: KProperty0<U>,
    defaultValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> =
    FallbackConfigProperty(fallbackProperty, defaultValue, transformer)

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 * @param defaultAndroidValue the value that the property evaluates to when there is no key with the name of the
 * property in the config and there is a configuration property in the rule set named "android" that is set to
 * <code>true</code>.
 */
fun <T : Any> configWithAndroidVariants(
    defaultValue: T,
    defaultAndroidValue: T,
): ReadOnlyProperty<ConfigAware, T> = configWithAndroidVariants(defaultValue, defaultAndroidValue) { it }

/**
 * Creates a delegated read-only property that can be used in [ConfigAware] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated and transformed only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int], [Boolean] and [List<String>] are supported.
 * @param defaultAndroidValue the value that the property evaluates to when there is no key with the name of the
 * property in the config and there is a configuration property in the rule set named "android" that is set to
 * <code>true</code>.
 * @param transformer a function that transforms the value from the configuration (or the default) into its final
 * value.
 */
fun <T : Any, U : Any> configWithAndroidVariants(
    defaultValue: T,
    defaultAndroidValue: T,
    transformer: (T) -> U
): ReadOnlyProperty<ConfigAware, U> =
    TransformedConfigPropertyWithAndroidVariants(defaultValue, defaultAndroidValue, transformer)

private fun <T : Any> getValueOrDefault(configAware: ConfigAware, propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (defaultValue) {
        is ExplainedValues -> configAware.getExplainedValuesOrDefault(propertyName, defaultValue) as T
        is List<*> -> configAware.getListOrDefault(propertyName, defaultValue) as T
        is String,
        is Boolean,
        is Int -> configAware.valueOrDefault(propertyName, defaultValue)
        else -> error(
            "${defaultValue.javaClass} is not supported for delegated config property '$propertyName'. " +
                "Use one of String, Boolean, Int or List<String> instead."
        )
    }
}

private fun ConfigAware.getListOrDefault(propertyName: String, defaultValue: List<*>): List<String> {
    return if (defaultValue.all { it is String }) {
        val defaultValueAsListOfStrings = defaultValue as List<String>
        valueOrDefaultCommaSeparated(propertyName, defaultValueAsListOfStrings)
    } else {
        error("Only lists of strings are supported. '$propertyName' is invalid. ")
    }
}

private fun ConfigAware.getExplainedValuesOrDefault(
    propertyName: String,
    defaultValue: ExplainedValues
): ExplainedValues {
    val valuesAsList: List<*> = valueOrNull(propertyName) ?: return defaultValue
    if (valuesAsList.all { it is String }) {
        return ExplainedValues(values = valuesAsList.map { ExplainedValue(it as String) })
    }
    if (valuesAsList.all { it is Map<*, *> }) {
        return ExplainedValues(
            valuesAsList
                .map { it as Map<*, *> }
                .map { dict ->
                    try {
                        ExplainedValue(
                            value = dict["value"] as String,
                            reason = dict["reason"] as String?
                        )
                    } catch (e: ClassCastException) {
                        throw Config.InvalidConfigurationError(e)
                    } catch (e: NullPointerException) {
                        throw Config.InvalidConfigurationError(e)
                    }
                }
        )
    }
    error("Only lists of strings or maps with keys 'value' and 'reason' are supported. '$propertyName' is invalid.")
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
    private val fallbackProperty: KProperty0<U>,
    private val defaultValue: T,
    private val transform: (T) -> U
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: ConfigAware, property: KProperty<*>): U {
        if (thisRef.isConfigured(property.name)) {
            return transform(getValueOrDefault(thisRef, property.name, defaultValue))
        }
        if (thisRef.isConfigured(fallbackProperty.name)) {
            return fallbackProperty.get()
        }
        return transform(defaultValue)
    }

    private fun ConfigAware.isConfigured(propertyName: String) = valueOrNull<Any>(propertyName) != null
}
