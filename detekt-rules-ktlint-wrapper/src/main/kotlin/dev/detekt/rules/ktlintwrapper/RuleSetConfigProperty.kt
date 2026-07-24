package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : Any> ruleSetConfig(defaultValue: T): ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> =
    RuleSetConfigPropertyDelegate(defaultValue, T::class)

class RuleSetConfigPropertyDelegate<T : Any>(val defaultValue: T, private val klass: KClass<T>) :
    ReadOnlyProperty<Any?, RuleSetConfigProperty<T>> {

    @Volatile private var value: RuleSetConfigProperty<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): RuleSetConfigProperty<T> =
        value ?: RuleSetConfigProperty(property.name, defaultValue, klass).also { value = it }
}

class RuleSetConfigProperty<T : Any>(val key: String, val defaultValue: T, private val klass: KClass<T>) {
    fun value(config: Config): T = config.valueOrNull(key, klass) ?: defaultValue
}
