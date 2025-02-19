package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> ruleSetConfig(defaultValue: T): ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> =
    RuleSetConfigPropertyDelegate(defaultValue)

private class RuleSetConfigPropertyDelegate<T : Any>(
    val defaultValue: T,
) : ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> {

    @Volatile private var value: RuleSetConfigProperty<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): RuleSetConfigProperty<T> =
        value ?: RuleSetConfigProperty(property.name, defaultValue).also { value = it }
}

class RuleSetConfigProperty<T : Any>(val key: String, val defaultValue: T) {
    fun value(config: Config): T = config.valueOrDefault(key, defaultValue)
}
