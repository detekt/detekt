@file:Suppress("unused")

package cases

class EqualsAlwaysReturnsConstant1 {
	override fun equals(other: Any?): Boolean {
		return true // violations
	}

	private fun equal() {
	}
}

object EqualsAlwaysReturnsConstant2 {
	override fun equals(other: Any?): Boolean {
		return false // violations
	}
}
