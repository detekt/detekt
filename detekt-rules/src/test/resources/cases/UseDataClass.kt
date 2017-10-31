package cases

@Suppress("unused")
class DataClassCandidate(val i: Int) // reports 1

@Suppress("unused")
class DataClassCandidateWithProperties(val i: Int) { // reports 1

	val i2: Int = 0
}

@Suppress("unused")
class NoDataClassCandidate(val i: Int) {

	val i2: Int = 0

	fun f() {
		println()
	}

	object Obj
}

class NoDataClassCandidateWithAdditionalMethod(val i: Int) {

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun toString(): String {
		return super.toString()
	}

	fun f1() {
		println()
	}
}

class DataClassCandidateWithOverriddenMethods(val i: Int) { // reports 1

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun toString(): String {
		return super.toString()
	}
}

sealed class NoDataClassbecauseItsSealed {
	data class Success(val any: Any) : NoDataClassbecauseItsSealed()
	data class Error(val error: Throwable) : NoDataClassbecauseItsSealed()
}
