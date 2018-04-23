package cases

@Suppress("unused")
class MaxLineLength {
	companion object {
		val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
	}

	val loremIpsumField = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."

	fun main() {
		val thisIsAVeryLongValName = "This is a very, very long String that will break the MaxLineLength"

		if (thisIsAVeryLongValName.length > "This is not quite as long of a String".length) {
			println("It's indeed a very long String")
		}

		val hello = anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot()
		val loremIpsum = getLoremIpsum()

		println(hello)
		println(loremIpsum)

	}

	fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot(): String {
		return "Hello"
	}

	fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
}
