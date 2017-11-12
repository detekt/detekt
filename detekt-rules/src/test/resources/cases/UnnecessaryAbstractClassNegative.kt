@file:Suppress("unused")

package cases

abstract class AbstractClassOk {

	abstract val i: Int
	fun f() { }
}

abstract class AbstractClassWithPrimaryConstructorConcretePropertyOk(val i: Int) {
	abstract fun f()
}
