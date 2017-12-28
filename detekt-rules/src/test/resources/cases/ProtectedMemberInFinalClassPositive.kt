@file:Suppress("unused", "RedundantVisibilityModifier", "ProtectedInFinal", "ConvertSecondaryConstructorToPrimary")

package cases

class ProtectedMemberInFinalClassPositive {

	protected var i1 = 0 // reports 1

	protected constructor(i1: Int) : super() { // reports 1
		this.i1 = i1
	}

	protected fun function() {} // reports 1

	protected inner class InnerClass1 { // reports 1
		protected val i = 0 // reports 1
	}

	inner class InnerClass2 {
		protected val i = 0 // reports 1
	}

	protected object InnerObject // reports 1

	protected companion object { // reports 1
		protected class A { // reports 1
			protected var x = 0 // reports 1
		}
	}
}

abstract class ClassWithAbstractCompanionMembers {

	protected companion object {
		protected class A {
			protected var x = 0 // reports 1
		}
	}
}

open class OpenClass {

	inner class InnerClass {
		protected val i = 0 // reports 1
	}
}

class FinalClassWithProtectedConstructor protected constructor() // reports 1
