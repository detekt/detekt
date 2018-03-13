@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

class NoDataClassCandidate(val i: Int) {

	val i2: Int = 0

	fun f() {
		println()
	}

	object Obj
}

class NoDataClassCandidateWithAdditionalMethod(val i: Int) {

	fun f1() {
		println()
	}
}

class NoDataClassCandidateWithOnlyPrivateCtor1 private constructor(val i: Int)

class NoDataClassCandidateWithOnlyPrivateCtor2 {

	@Suppress("ConvertSecondaryConstructorToPrimary")
	private constructor(i: Int)
}

sealed class NoDataClassBecauseItsSealed {
	data class Success(val any: Any) : NoDataClassBecauseItsSealed()
	data class Error(val error: Throwable) : NoDataClassBecauseItsSealed()
}

enum class EnumNoDataClass(val i: Int) {

	FIRST(1), SECOND(2);
}

annotation class AnnotationNoDataClass(val i: Int)
