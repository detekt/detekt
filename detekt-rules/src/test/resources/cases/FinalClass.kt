package cases

// should report 9 for protected = internal
@Suppress("unused")
class FinalClass : BaseClass {

	private val i = 0
	protected var i1 = 0 // positive case

	protected constructor(i1: Int) : super() { // positive case
		this.i1 = i1
	}

	// should not report protected = private visibility
	protected override val abstractProp = 0

	// should not report protected = private visibility
	protected override fun abstractFunction() {
	}


	protected fun function() {
	} // positive case

	protected inner class InnerClass1 { // positive case

		protected val i = 0 // positive case
	}

	inner class InnerClass2 {

		protected val i = 0 // positive case
	}

	protected object InnerObject // positive case
}

@Suppress("unused")
abstract class BaseClass {

	protected abstract val abstractProp: Int
	protected abstract fun abstractFunction()
}

@Suppress("unused")
open class OpenClass {

	inner class InnerClass {

		// positive case
		protected val i = 0
	}
}

// positive case
class FinalClassWithProtectedConstructor protected constructor()
