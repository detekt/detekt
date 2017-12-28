@file:Suppress("unused")

package cases

// reports 2 - for each defined function in the data class
data class DataClassWithFunctions(val i: Int) {

	fun f1() {}
	fun f2() {}

	// reports 1 - for each defined conversion function in the data class
	data class NestedDataClassWithConversionFunction(val i : Int) {
		fun toDataClassWithOverriddenMethods() = DataClassWithOverriddenMethods(i)
	}
}
