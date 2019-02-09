package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class LongMethodPositive {

    fun longMethod() { // 5 lines
        println()
        println()
        println()

        fun nestedLongMethod() { // 5 lines
            println()
            println()
            println()
        }
    }
}
