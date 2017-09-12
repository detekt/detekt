package cases

import java.util.NoSuchElementException

@Suppress("unused")
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

@Suppress("unused")
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

@Suppress("unused")
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

@Suppress("unused")
abstract class AbstractIterator1 : Iterator<String>

@Suppress("unused")
// violation NotThrowingNoSuchElementException, HasNextCallsNextMethod
abstract class AbstractIterator2 : Iterator<String> {

	override fun hasNext(): Boolean {
		if (1 == 1) {
			next()
		}
		return true
	}

	override fun next(): String {
		return ""
	}
}

@Suppress("unused")
class NoIteratorImpl
