@file:Suppress("unused", "ConstantConditionIf")

package cases

// reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
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

    // reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
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

// reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
interface InterfaceIterator : Iterator<String> {

    override fun hasNext(): Boolean {
        next()
        return true
    }

    override fun next(): String {
        return ""
    }
}

// reports IteratorNotThrowingNoSuchElementException, IteratorHasNextCallsNextMethod
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
