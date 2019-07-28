package io.gitlab.arturbosch.detekt.api

import kotlin.reflect.KProperty

/**
 * Allows to assign a property just once.
 * Further assignments result in [IllegalStateException]'s.
 */
class SingleAssign<T : Any> {

    private lateinit var _value: T

    /**
     * Gets the stored value if it is initialized or else an error is thrown.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        check(this::_value.isInitialized) { "Property ${property.name} has not been assigned yet!" }
        return _value
    }

    /**
     * Only sets the value if it was not yet set or else an error is thrown
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        check(!this::_value.isInitialized) { "Property ${property.name} has already been assigned!" }
        _value = value
    }
}
