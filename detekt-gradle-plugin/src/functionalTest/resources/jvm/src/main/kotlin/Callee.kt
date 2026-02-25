package jvm.src.main.kotlin

import kotlin.system.exitProcess
import kotlinx.serialization.Serializable

class Callee {
    fun forbiddenMethod() {
        error("don't ever call this method")
        exitProcess(0)
    }
}

@Serializable
data class Book(val name: String, val author: String)
