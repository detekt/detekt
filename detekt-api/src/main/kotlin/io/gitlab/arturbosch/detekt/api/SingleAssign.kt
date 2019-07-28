package io.gitlab.arturbosch.detekt.api

import kotlin.reflect.KProperty

/**
 * Allows to assign a property just once.
 * Further assignments result in [IllegalStateException]'s.
 */
class SingleAssign<T : Any> {

    private lateinit var _value: T

    /**
     * Returns the [_value] if it was set before. Else an error is thrown.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        check(this::_value.isInitialized) { "Property ${property.name} has not been assigned yet!" }
        return _value
    }

    /**
     * Sets [_value] to the given [value]. If it was set before, an error is thrown.
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        check(!this::_value.isInitialized) { "Property ${property.name} has already been assigned!" }
        _value = value
    }
}
