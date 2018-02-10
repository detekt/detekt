@file:Suppress("unused", "ConstantConditionIf")

package cases

/**
 * @author Artur Bosch
 */
class Empty : Runnable {

	init {

	}

	constructor() {

	}

	override fun run() {

	}

	fun emptyMethod() {

	}

	fun stuff() {
		try {

		} catch (e: Exception) {

		} catch (e: Exception) {
			//no-op
		} catch (e: Exception) {
			println()
		} catch (ignored: Exception) {

		} catch (expected: Exception) {

		}finally {

		}
		if (true) {

		} else {

		}
		when (true) {

		}
		for (i in 1..10) {

		}
		while (true) {

		}
		do {

		} while (true)
	}
}

class EmptyClass() {}
