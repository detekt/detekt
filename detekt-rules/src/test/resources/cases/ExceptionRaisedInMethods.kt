@file:Suppress("unused", "ConstantConditionIf")

package cases

/**
 * This class contains methods which raise exceptions
 */
open class ExceptionRaisedInMethods {

	init {
		throw IllegalStateException() // violation
	}

	override fun toString(): String {
		throw IllegalStateException() // violation
	}

	override fun hashCode(): Int {
		throw IllegalStateException() // violation
	}

	override fun equals(other: Any?): Boolean {
		throw IllegalStateException() // violation
	}

	protected fun finalize() {
		if (true) {
			throw IllegalStateException() // violation
		}
	}

	companion object {
		init {
			throw IllegalStateException() // violation
		}
	}
}

object ExceptionRaisedInMethodsObject {

	override fun equals(other: Any?): Boolean {
		throw IllegalStateException() // violation
	}
}

open class NoExceptionRaisedInMethods {

	override fun toString(): String {
		return super.toString()
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	companion object {
		init {
		}
	}

	protected fun finalize() {
	}
}
