@file:Suppress("EqualsOrHashCode", "ConstantConditionIf")

package cases

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

                try {
                    for (i in 1..5) {
                        when (i) {
                            1 -> print(1)
                        }
                    }
                } finally {

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
val aTopLevelPropertyOfNestedClasses = 0
