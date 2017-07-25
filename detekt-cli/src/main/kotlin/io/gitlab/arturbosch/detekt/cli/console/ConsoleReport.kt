package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import java.io.PrintStream
import kotlin.reflect.KProperty

/**
 * @author Artur Bosch
 */
abstract class ConsoleReport {

	open val id: String = javaClass.simpleName
	open val priority: Int = -1

	@Suppress("EmptyFunctionBlock")
	open fun init(config: Config) {
	}

	fun print(printer: PrintStream, detektion: Detektion) {
		render(detektion)?.let {
			printer.println(it)
		}
	}

	abstract fun render(detektion: Detektion): String?
}

const val PREFIX = "\t- "

fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"

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

	companion object UNINITIALIZED_VALUE
}
