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

interface TooLargeInterfaceWithStaticDeclarations {
	fun f1()

	companion object {
		fun sf() = 0
		const val c = 0
		val si = 0
	}
}

interface InterfaceOk1 {
	fun f1()
	fun fImpl() {
		val x = 0 // should not report
	}
	val i1: Int
	// a comment shouldn't be detected
}

interface InterfaceOk2 {
	fun f1()

	companion object {
		fun sf() = 0
		const val c = 0
	}
}

interface EmptyInterface
