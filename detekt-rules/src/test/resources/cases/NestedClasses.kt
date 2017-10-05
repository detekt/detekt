@file:Suppress("EqualsOrHashCode", "ConstantConditionIf")

package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class NestedClasses {

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	class InnerClass {

		override fun equals(other: Any?): Boolean {
			return super.equals(other)
		}

		class NestedInnerClass {

			override fun hashCode(): Int {
				return super.hashCode()
			}

			fun nestedLongMethod() {
				if (true) {
					if (true) {
						if (true) {
							5.run {
								this.let {
									listOf(1, 2, 3).map { it * 2 }
											.groupBy(Int::toString, Int::toString)
								}
							}
						}
					}
				}

				fun nestedLocalMethod() {
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
					println()
				}
				nestedLocalMethod()
			}
		}
	}

}
