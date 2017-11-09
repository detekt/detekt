@file:Suppress("unused")

package cases

data class DataClassWithFunctions(val i: Int) { // reports 2

	fun f1() {}
	fun f2() {}

	data class NestedDataClassWithConversionFunction(val i : Int) { // reports 1
		fun toDataClassWithOverriddenMethods() = DataClassWithOverriddenMethods(i)
	}
}
