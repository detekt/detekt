@file:Suppress("unused", "ConstantConditionIf")

package cases

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
			next()
			return true
		}

		override fun next(): String {
			throw IllegalStateException()
		}
	}
}

// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
interface InterfaceIterator : Iterator<String> {

	override fun hasNext(): Boolean {
		next()
		return true
	}

	override fun next(): String {
		return ""
	}
}

// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
abstract class AbstractIterator : Iterator<String> {

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
