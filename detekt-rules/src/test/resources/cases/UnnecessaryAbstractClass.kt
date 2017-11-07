@file:Suppress("unused")

package cases

abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members

	abstract val i: Int
	abstract fun f()
}

abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members

	val i: Int = 0
	fun f() { }
}

abstract class AbstractClassOk {

	abstract val i: Int
	fun f() { }
}

class ConcreteClass {

	abstract class NestedAbstractClass { // violation: no abstract members
		fun f() { }
	}
}

interface AbstractInterface {

	abstract class NestedAbstractClass { // violation: no abstract members
		fun f() { }
	}
}

abstract class AbstractClassWithPrimaryConstructorConcretePropertyOk(val i: Int) {
	abstract fun f()
}
