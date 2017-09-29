@file:Suppress("unused", "UNUSED_VARIABLE")

package cases

interface TooLargeInterface {
	fun f1()
	fun f2()
	val i1: Int
	fun fImpl() { }
}

class ClassWithNestedInterface {

	interface TooLargeNestedInterface {
		fun f1()
		fun f2()
		val i1: Int
		fun fImpl() { }
	}
}

interface InterfaceOk {
	fun f1()
	fun fImpl() {
		val x = 0 // should not report
	}
	val i1: Int
	// a comment shouldn't be detected
}

interface EmptyInterface
