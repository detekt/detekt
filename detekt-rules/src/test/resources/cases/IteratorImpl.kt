@file:Suppress("unused", "ConstantConditionIf", "UNUSED_PARAMETER")

package cases

import java.util.NoSuchElementException

class IteratorImplPositive : Iterator<String> {

	override fun hasNext(): Boolean {
		next(1)
		return true
	}

	override fun next(): String {
		if (!hasNext()) throw NoSuchElementException()
		return ""
	}

	// next method overload
	private fun next(i: Int) {
	}
}

// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
class IteratorImpl2 : Iterator<String> {

	override fun hasNext(): Boolean {
		next()
		return true
	}

	override fun next(): String {
		return ""
	}
}

class IteratorImplContainer {

	// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
	object IteratorImplNegative3 : Iterator<String> {

		override fun hasNext(): Boolean {
			return true
		}

		override fun next(): String {
			throw IllegalStateException()
		}
	}
}

// violation NotThrowingNoSuchElementException
interface InterfaceIterator : Iterator<String> {

	override fun next(): String {
		return ""
	}
}

abstract class AbstractIterator1 : Iterator<String>


// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
abstract class AbstractIterator2 : Iterator<String> {

	override fun hasNext(): Boolean {
		if (true) {
			next()
		}
		return true
	}

	override fun next(): String {
		return ""
	}
}

class NoIteratorImpl
