// we can't suppress warnings from intellij here as we support UNUSED_VARIABLE as an alias

package cases

/**
 * Many false positives reported in #812 - https://github.com/arturbosch/detekt/issues/812
 * and #840 - https://github.com/arturbosch/detekt/pull/840.
 */
object O { // public
	const val NUMBER = 5 // public
}

private object PO { // private, but constants may be used
	const val TEXT = "text"
}

class C {
	private val unusedField = 5					// unused field
	val myNumber = 5							// public and unused

	private fun unusedFunction(unusedParam: Int, // unused local param
							   usedParam: String) { // used local param
		val unusedLocal = 5                            // unused local var
		println(usedParam)
		println(PC.THE_CONST)
		println("Hello " ext "World" ext "!")
		println(::doubleColonObjectReferenced)
		println(this::doubleColonThisReferenced)
	}

	fun usesAllowedNames() {
		for ((index, _) in mapOf(0 to 0, 1 to 1, 2 to 2)) {
			println(index)
		}
		try {
		} catch (_: OutOfMemoryError) {
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

private class PC {                                    // used private class
	companion object {
		internal const val THE_CONST = ""            // used private const
		object OO {
			const val BLA = 4
		}
	}
}

private fun unusedFunction() = 5                    // unused

internal fun libraryFunction() = run {
	val o: Function1<Any, Any> = object : Function1<Any, Any> {
		override fun invoke(p1: Any): Any { // unused but overridden param
			throw UnsupportedOperationException("not implemented")
		}
	}
	println(o("${PC.Companion.OO.BLA.toString() + ""}"))
}

internal class IC                                    // unused but internal

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
}
