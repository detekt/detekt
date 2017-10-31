package cases

@Suppress("unused")
abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members

	abstract val i: Int
	abstract fun f()
}

@Suppress("unused")
abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members

	val i: Int = 0
	fun f() { }
}

@Suppress("unused")
abstract class AbstractClassOk {

	abstract val i: Int
	fun f() { }
}

@Suppress("unused")
class ConcreteClass {

	abstract class NestedAbstractClass { // violation: no abstract members
		fun f() { }
	}
}

@Suppress("unused")
interface AbstractInterface {

	abstract class NestedAbstractClass { // violation: no abstract members
		fun f() { }
	}
}
