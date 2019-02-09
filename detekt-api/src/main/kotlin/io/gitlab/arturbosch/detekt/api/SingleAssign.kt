package io.gitlab.arturbosch.detekt.api

import kotlin.reflect.KProperty

/**
 * Allows to assign a property just once.
 * Further assignments result in [IllegalStateException]'s.
 *
 * @author Artur Bosch
 */
class SingleAssign<T : Any> {

    private lateinit var _value: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        check(this::_value.isInitialized) { "Property ${property.name} has not been assigned yet!" }
        return _value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        check(!this::_value.isInitialized) { "Property ${property.name} has already been assigned!" }
        _value = value
    }
}
