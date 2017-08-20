@file:Suppress("unused")

package cases

/**
 * @author Artur Bosch
 */
class FeatureEnvy1 {

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

class FeatureEnvy2 {

	val envy1 = FeatureEnvy1()

	fun envy() {
		envy1.envy()
	}
}
