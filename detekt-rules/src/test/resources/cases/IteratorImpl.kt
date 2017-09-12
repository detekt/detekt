package cases

import java.util.NoSuchElementException

@Suppress("unused")
class IteratorImplPositive : Iterator<String> {

	override fun hasNext(): Boolean {
		return true
	}

	override fun next(): String {
		if (!hasNext()) throw NoSuchElementException()
		return ""
	}

	// next method overload
	fun next(i: Int) {

	}
}

@Suppress("unused")
abstract class AbstractIterator1 : Iterator<String>

@Suppress("unused")
abstract class AbstractIterator2 : Iterator<String> { // violation NotThrowingNoSuchElementException

	override fun hasNext(): Boolean {
		return true
	}

	override fun next(): String {
		return ""
	}
}

@Suppress("unused")
class NoIteratorImpl

@Suppress("unused")
class IteratorImplNegative1 : Iterator<String> { // violation NotThrowingNoSuchElementException

	override fun hasNext(): Boolean {
		return true
	}

	override fun next(): String {
		return ""
	}
}

@Suppress("unused")
class IteratorImplContainer {

	object IteratorImplNegative2 : Iterator<String> { // violation NotThrowingNoSuchElementException

		override fun hasNext(): Boolean {
			return true
		}

		override fun next(): String {
			throw IllegalStateException()
		}
	}
}
