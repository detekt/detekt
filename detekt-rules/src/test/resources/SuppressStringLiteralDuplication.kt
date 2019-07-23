@file:Suppress("unused", "UNUSED_PARAMETER", "UnusedEquals")

@Suppress("StringLiteralDuplication")
class Duplication {
    var s1 = "lorem"
    fun f(s: String = "lorem") {
        s1 == "lorem"
    }
}
