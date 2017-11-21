@file:Suppress("EqualsOrHashCode", "ConstantConditionIf")

package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class NestedClasses {

	private val i = 0

	class InnerClass {

		class NestedInnerClass {

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

@Suppress("unused")
/**
 * Top level members must be skipped for LargeClass rule
 */
val aTopLevelProperty = 0
