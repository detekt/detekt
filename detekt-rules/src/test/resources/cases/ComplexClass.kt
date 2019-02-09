package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused", "ConstantConditionIf")
fun complex() { //20
    try {//5
        while (true) {
            if (true) {
                when ("string") {
                    "" -> println()
                    else -> println()
                }
            }
        }
    } catch (ex: Exception) { //1 + 5
        try {
            println()
        } catch (ex: Exception) {
            while (true) {
                if (false) {
                    println()
                } else {
                    println()
                }
            }
        }
    } finally { // 6
        try {
            println()
        } catch (ex: Exception) {
            while (true) {
                if (false) {
                    println()
                } else {
                    println()
                }
            }
        }
    }
    (1..10).forEach {
        //1
        println()
    }
    for (i in 1..10) { //1
        println()
    }
}
