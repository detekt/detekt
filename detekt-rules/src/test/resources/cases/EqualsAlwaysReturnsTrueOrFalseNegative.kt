@file:Suppress("EqualsOrHashCode", "unused", "UNREACHABLE_CODE")

package cases

class EqualsReturnsTrueOrFalse {

	override fun equals(other: Any?): Boolean {
		if (other is Int) {
			return true
		}
		return false
	}
}

class CorrectEquals {

	override fun equals(other: Any?): Boolean {
		return this.toString() == other.toString()
	}
}

fun equals(other: Any?): Boolean {
	return false
}

class NotOverridingEquals {

	fun equal(other: Any?): Boolean {
		return true
	}
}

class WrongEqualsParameterList {

	fun equals(other: Any, i: Int): Boolean {
		return true
	}
}
