package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> ruleSetConfig(defaultValue: T): ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> =
    RuleSetConfigPropertyDelegate(defaultValue)

private class RuleSetConfigPropertyDelegate<T : Any>(
    val defaultValue: T
) : ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> {

    @Volatile private var _value: RuleSetConfigProperty<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): RuleSetConfigProperty<T> =
        _value ?: RuleSetConfigProperty(property.name, defaultValue).also { _value = it }
}

class RuleSetConfigProperty<T : Any>(val key: String, val defaultValue: T) {
    fun value(config: Config): T =
        config.valueOrDefault(key, defaultValue)
}
