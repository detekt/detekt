package jvm.src.main.kotlin

import kotlin.system.exitProcess

class Callee {
    fun forbiddenMethod() {
        error("don't ever call this method")
        exitProcess(0)
    }
}
