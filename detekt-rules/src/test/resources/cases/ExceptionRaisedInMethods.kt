@file:Suppress("unused", "ConstantConditionIf", "RedundantOverride", "EqualsOrHashCode")

package cases

/**
 * This class contains methods which raise exceptions
 */
open class ExceptionRaisedInMethods {

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
}

object ExceptionRaisedInMethodsObject {

	override fun equals(other: Any?): Boolean {
		throw IllegalStateException() // violation
	}
}

open class NoExceptionRaisedInMethods {

	init {
		throw IllegalStateException()
	}

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
			throw IllegalStateException()
		}
	}

	protected fun finalize() {
	}
}
