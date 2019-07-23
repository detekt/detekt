@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

import java.util.NoSuchElementException

class IteratorImplOk : Iterator<String> {

    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): String {
        if (!hasNext()) throw NoSuchElementException()
        return ""
    }

    // next method overload should not be reported
    private fun next(i: Int) {
    }
}

class NoIteratorImpl

abstract class AbstractIteratorNotOverridden : Iterator<String>
