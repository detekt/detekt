@file:Suppress("unused", "RedundantVisibilityModifier", "ProtectedInFinal", "ConvertSecondaryConstructorToPrimary")

package cases

class ProtectedMemberInFinalClassPositive {

	protected var i1 = 0 // positive case

	protected constructor(i1: Int) : super() { // positive case
		this.i1 = i1
	}

	protected fun function() {} // positive case

	protected inner class InnerClass1 { // positive case
		protected val i = 0 // positive case
	}

	inner class InnerClass2 {
		protected val i = 0 // positive case
	}

	protected object InnerObject // positive case

	protected companion object { // positive case
		protected class A { // positive case
			protected var x = 0 // positive case
		}
	}
}

abstract class ClassWithAbstractCompanionMembers {

	protected companion object {
		protected class A {
			protected var x = 0 // positive case
		}
	}
}

open class OpenClass {

	inner class InnerClass {
		protected val i = 0 // positive case
	}
}

class FinalClassWithProtectedConstructor protected constructor() // positive case
