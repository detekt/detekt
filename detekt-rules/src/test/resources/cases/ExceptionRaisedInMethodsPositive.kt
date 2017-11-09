@file:Suppress("unused", "ConstantConditionIf", "RedundantOverride", "EqualsOrHashCode")

package cases

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
