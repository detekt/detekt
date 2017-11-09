@file:Suppress("unused")

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
