@file:Suppress("unused")

package cases

/**
 * @author Artur Bosch
 */
class FeatureEnvy {

	object Stuff {
		fun doStuff() {}
		fun giveStuff() = ""
	}

	/**
	 * 5 calls, 4 from Stuff object
	 */
	fun envy() {
		val stuff = Stuff
		stuff.doStuff()
		stuff.doStuff()
		stuff.doStuff()
		val string = stuff.giveStuff()
		println(string)
	}
}