@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

// reports all x() methods over overload threshold
class OverloadedMethods {

	fun x() {}
	fun x(i: Int) {}
	fun x(i: Int, j: Int) {}

	fun y() {}
	fun y(i: Int) {}

	private class InnerClass {

		fun x() {}
		fun x(i: Int) {}
		fun x(i: Int, j: Int) {}
	}
}

fun overloadedMethod() {}
fun overloadedMethod(i: Int) {}
fun overloadedMethod(i: Int, j: Int) {}
