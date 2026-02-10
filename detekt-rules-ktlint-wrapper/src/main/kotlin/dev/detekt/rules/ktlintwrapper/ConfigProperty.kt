package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.api.valueOrDefault
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Creates a delegated read-only property that can be used in [Rule] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int] and [Boolean] are supported.
 * @param defaultAndroidValue the value that the property evaluates to when there is no key with the name of the
 * property in the config and there is a configuration property in the rule set named "android" that is set to
 * <code>true</code>.
 */
internal fun <T : Any> configWithAndroidVariants(defaultValue: T, defaultAndroidValue: T): ReadOnlyProperty<Rule, T> =
    configWithAndroidVariants(defaultValue, defaultAndroidValue) { it }

/**
 * Creates a delegated read-only property that can be used in [Rule] objects. The name of the property is the
 * key that is used during configuration lookup. The value of the property is evaluated and transformed only once.
 *
 * @param defaultValue the value that the property evaluates to when there is no key with the name of the property in
 * the config. Although [T] is defined as [Any], only [String], [Int] and [Boolean] are supported.
 * @param defaultAndroidValue the value that the property evaluates to when there is no key with the name of the
 * property in the config and there is a configuration property in the rule set named "android" that is set to
 * <code>true</code>.
 * @param transformer a function that transforms the value from the configuration (or the default) into its final
 * value.
 */
internal fun <T : Any, U : Any> configWithAndroidVariants(
    defaultValue: T,
    defaultAndroidValue: T,
    transformer: (T) -> U,
): ReadOnlyProperty<Rule, U> =
    TransformedConfigPropertyWithAndroidVariants(defaultValue, defaultAndroidValue, transformer)

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

private abstract class MemoizedConfigProperty<U : Any> : ReadOnlyProperty<Rule, U> {
    private var value: U? = null

    override fun getValue(thisRef: Rule, property: KProperty<*>): U =
        value ?: doGetValue(thisRef, property).also { value = it }

    abstract fun doGetValue(thisRef: Rule, property: KProperty<*>): U
}

private fun <T : Any> getValueOrDefault(config: Config, propertyName: String, defaultValue: T): T =
    @Suppress("UNCHECKED_CAST")
    when (defaultValue) {
        is String -> config.valueOrDefault<String>(propertyName, defaultValue) as T

        is Boolean -> config.valueOrDefault<Boolean>(propertyName, defaultValue) as T

        is Int -> config.valueOrDefault<Int>(propertyName, defaultValue) as T

        else -> error(
            "${defaultValue.javaClass} is not supported for delegated config property '$propertyName'. " +
                "Use one of String, Boolean or Int."
        )
    }
