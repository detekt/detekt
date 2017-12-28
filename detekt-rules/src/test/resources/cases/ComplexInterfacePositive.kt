@file:Suppress("unused")

package cases

// reports 1 - too many members
interface TooLargeInterface {
	fun f1()
	fun f2()
	val i1: Int
	fun fImpl() { }
}

// reports 1 - too many members
class ClassWithNestedInterface {

	interface TooLargeNestedInterface {
		fun f1()
		fun f2()
		val i1: Int
		fun fImpl() { }
	}
}

// reports 1 - too many members
interface TooLargeInterfaceWithStaticDeclarations {
	fun f1()

	companion object {
		fun sf() = 0
		const val c = 0
		val si = 0
	}
}
