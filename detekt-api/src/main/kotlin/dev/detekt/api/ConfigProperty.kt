package dev.detekt.api

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
    defaultValue: T,
): ReadOnlyProperty<Rule, T> = config(defaultValue) { it }

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
    transformer: (T) -> U,
): ReadOnlyProperty<Rule, U> = TransformedConfigProperty(defaultValue, transformer)

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
fun <T : Any> configWithFallback(
    fallbackProperty: KProperty0<T>,
    defaultValue: T,
): ReadOnlyProperty<Rule, T> = configWithFallback(fallbackProperty, defaultValue) { it }

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
fun <T : Any, U : Any> configWithFallback(
    fallbackProperty: KProperty0<U>,
    defaultValue: T,
    transformer: (T) -> U,
): ReadOnlyProperty<Rule, U> =
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
): ReadOnlyProperty<Rule, T> = configWithAndroidVariants(defaultValue, defaultAndroidValue) { it }

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
    transformer: (T) -> U,
): ReadOnlyProperty<Rule, U> =
    TransformedConfigPropertyWithAndroidVariants(defaultValue, defaultAndroidValue, transformer)

private fun <T : Any> getValueOrDefault(config: Config, propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (defaultValue) {
        is ValuesWithReason -> config.getValuesWithReasonOrDefault(propertyName, defaultValue) as T
        is List<*> -> config.getListOrDefault(propertyName, defaultValue) as T
        is String,
        is Boolean,
        is Int,
        -> config.valueOrDefault(propertyName, defaultValue)

        else -> error(
            "${defaultValue.javaClass} is not supported for delegated config property '$propertyName'. " +
                "Use one of String, Boolean, Int or List<String> instead."
        )
    }
}

private fun Config.getListOrDefault(propertyName: String, defaultValue: List<*>): List<String> =
    if (defaultValue.all { it is String }) {
        @Suppress("UNCHECKED_CAST")
        val defaultValueAsListOfStrings = defaultValue as List<String>
        valueOrDefault(propertyName, defaultValueAsListOfStrings)
    } else {
        error("Only lists of strings are supported. '$propertyName' is invalid. ")
    }

private fun Config.getValuesWithReasonOrDefault(
    propertyName: String,
    defaultValue: ValuesWithReason,
): ValuesWithReason {
    val valuesAsList: List<*> = valueOrNull(propertyName) ?: return defaultValue
    if (!valuesAsList.all { it is String || it is Map<*, *> }) {
        error(
            "Only lists of strings and/or maps with keys 'value' and 'reason' are supported. " +
                "'$propertyName' is invalid."
        )
    }
    return ValuesWithReason(
        valuesAsList.map {
            if (it is String) {
                ValueWithReason(it)
            } else {
                val mapValue = it as Map<*, *>
                try {
                    ValueWithReason(
                        value = mapValue["value"] as String,
                        reason = mapValue["reason"] as String?
                    )
                } catch (e: ClassCastException) {
                    throw Config.InvalidConfigurationError(e)
                } catch (@Suppress("TooGenericExceptionCaught") e: NullPointerException) {
                    throw Config.InvalidConfigurationError(e)
                }
            }
        }
    )
}

private abstract class MemoizedConfigProperty<U : Any> : ReadOnlyProperty<Rule, U> {
    private var value: U? = null

    override fun getValue(thisRef: Rule, property: KProperty<*>): U =
        value ?: doGetValue(thisRef, property).also { value = it }

    abstract fun doGetValue(thisRef: Rule, property: KProperty<*>): U
}

private class TransformedConfigPropertyWithAndroidVariants<T : Any, U : Any>(
    private val defaultValue: T,
    private val defaultAndroidValue: T,
    private val transform: (T) -> U,
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: Rule, property: KProperty<*>): U {
        val rulesetConfig = requireNotNull(thisRef.config.parent) {
            "A rule that uses the 'configWithAndroidVariants' property delegate must have a parent config."
        }
        val isAndroid = getValueOrDefault(rulesetConfig, "code_style", "") == "android_studio"
        val value = if (isAndroid) defaultAndroidValue else defaultValue
        return transform(getValueOrDefault(thisRef.config, property.name, value))
    }
}

private class TransformedConfigProperty<T : Any, U : Any>(
    private val defaultValue: T,
    private val transform: (T) -> U,
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: Rule, property: KProperty<*>): U =
        transform(getValueOrDefault(thisRef.config, property.name, defaultValue))
}

private class FallbackConfigProperty<T : Any, U : Any>(
    private val fallbackProperty: KProperty0<U>,
    private val defaultValue: T,
    private val transform: (T) -> U,
) : MemoizedConfigProperty<U>() {
    override fun doGetValue(thisRef: Rule, property: KProperty<*>): U {
        if (thisRef.config.isConfigured(property.name)) {
            return transform(getValueOrDefault(thisRef.config, property.name, defaultValue))
        }
        if (thisRef.config.isConfigured(fallbackProperty.name)) {
            return fallbackProperty.get()
        }
        return transform(defaultValue)
    }

    private fun Config.isConfigured(propertyName: String) = valueOrNull<Any>(propertyName) != null
}
