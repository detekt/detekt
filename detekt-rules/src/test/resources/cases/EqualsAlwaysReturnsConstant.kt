@file:Suppress("unused")

package cases

class EqualsAlwaysReturnsConstant1 {
	override fun equals(other: Any?): Boolean {
		return true // violations
	}

	fun equal(): Boolean {
		return true
	}
}

object EqualsAlwaysReturnsConstant2 {
	override fun equals(other: Any?): Boolean {
		return this == other
	}
}

fun equals(other: Any?): Boolean {
	return false;
}
