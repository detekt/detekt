package io.gitlab.arturbosch.detekt.api

import kotlin.reflect.KProperty

/**
 * @author Artur Bosch
 */
class SingleAssign<T> {

	private var initialized = false
	private var _value: Any? = UNINITIALIZED_VALUE

	operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		if (!initialized) {
			throw IllegalStateException("Property ${property.name} has not been assigned yet!")
		}
		@Suppress("UNCHECKED_CAST")
		return _value as T
	}

	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		if (initialized) {
			throw IllegalStateException("Property ${property.name} has already been assigned!")
		}
		_value = value
		initialized = true
	}

	companion object {
		private val UNINITIALIZED_VALUE = Any()
	}
}
