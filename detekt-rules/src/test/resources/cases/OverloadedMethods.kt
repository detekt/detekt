@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

class OverloadedMethods {

	// reports 1 - x() method overload count exceeds threshold
	fun x() {}
	fun x(i: Int) {}
	fun x(i: Int, j: Int) {}

	private class InnerClass {

		// reports 1 - x() method overload count exceeds threshold
		fun x() {}
		fun x(i: Int) {}
		fun x(i: Int, j: Int) {}
	}
}

// reports 1 - overloadedMethod() method overload count exceeds threshold
fun overloadedMethod() {}
fun overloadedMethod(i: Int) {}
fun overloadedMethod(i: Int, j: Int) {}
