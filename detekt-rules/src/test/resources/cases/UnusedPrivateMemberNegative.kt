// we can't suppress warnings from intellij here as we support UNUSED_VARIABLE as an alias

package cases

import kotlin.reflect.KProperty

object O { // public
    const val NUMBER = 5 // public
}

private object PO { // private, but constants may be used
    const val TEXT = "text"
}

class C {
    val myNumber = 5

    fun publicFunction(usedParam: String) {
        println(usedParam)
        println(PC.THE_CONST)
        println("Hello " ext "World" ext "!")
        println(::doubleColonObjectReferenced)
        println(this::doubleColonThisReferenced)
    }

    fun usesAllowedNames() {
        for ((index, _) in mapOf(0 to 0, 1 to 1, 2 to 2)) {  // unused but allowed name
            println(index)
        }
        try {
        } catch (_: OutOfMemoryError) { // unused but allowed name
        }
    }

    private fun doubleColonThisReferenced() {}

    companion object {
        private infix fun String.ext(other: String): String {
            return this + other
        }

        private fun doubleColonObjectReferenced() {}
    }
}

private class PC { // used private class
    companion object {
        internal const val THE_CONST = "" // used private const

        object OO {
            const val BLA = 4
        }
    }
}

internal fun libraryFunction() = run {
    val o: Function1<Any, Any> = object : Function1<Any, Any> {
        override fun invoke(p1: Any): Any { // unused but overridden param
            throw UnsupportedOperationException("not implemented")
        }
    }
    println(o("${PC.Companion.OO.BLA.toString() + ""}"))
}

internal class IC // unused but internal

val stuff = object : Iterator<String?> {

    var mutatable: String? = null

    private fun preCall() {
        mutatable = "done"
    }

    override fun next(): String? {
        preCall()
        return mutatable
    }

    override fun hasNext(): Boolean = true
}

fun main(args: Array<String>) {
    println(stuff.next())
    calledFromMain()
}

private fun calledFromMain() {}

abstract class Parent {
    abstract fun abstractFun(arg: Any)
    open fun openFun(arg: Any): Int = 0
}

class Child : Parent() {
    override fun abstractFun(arg: Any) {
        println(arg)
    }

    override fun openFun(arg: Any): Int {
        println(arg)
        return 1
    }
}

class SingleAssign<String> {

    // ignore unused operator function parameters
    operator fun getValue(thisRef: Any?, property: KProperty<*>): kotlin.String {
        return ""
    }
}
