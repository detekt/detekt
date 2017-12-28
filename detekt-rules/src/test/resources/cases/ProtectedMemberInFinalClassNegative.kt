@file:Suppress("unused", "RedundantVisibilityModifier", "ProtectedInFinal", "ConvertSecondaryConstructorToPrimary")

package cases

class NoProtectedMembersInFinalClass : BaseClass() {

	private val i = 0

	// should not report protected = private visibility
	protected override val abstractProp = 0

	// should not report protected = private visibility
	protected override fun abstractFunction() {
	}
}

abstract class BaseClass {

	protected abstract val abstractProp: Int
	protected abstract fun abstractFunction()

	protected object InnerObject
}

sealed class SealedClass {

	protected fun a() {}
}


