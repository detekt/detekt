@file:Suppress("unused")

package cases

class LongMethodNegative {

    fun methodOk() { // 3 lines
        println()
        fun localMethodOk() { // 4 lines
            println()
            println()
        }
    }
}
