package jvm.src.main.kotlin

class Callee {
    fun forbiddenMethod() {
        error("don't ever call this method")
    }
}
