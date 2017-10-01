@file:Suppress("unused")

package cases

data class ValidDataClass(val i: Int)

data class DataClassWithFunctions(val i: Int) { // reports 2

	fun f1() {
		println()
	}

	fun f2() {
		println()
	}
}

data class DataClassWithOverriddenMethods(val i: Int) {

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	override fun toString(): String {
		return super.toString()
	}
}

class ClassWithRegularFunctions {

	fun f1() {
		println()
	}

	fun f2() {
		println()
	}
}


@Suppress("unused")
data class DataClassWithConversionFunction(val i : Int) {
	fun toDataClassWithOverriddenMethods() = DataClassWithOverriddenMethods(i)
}
