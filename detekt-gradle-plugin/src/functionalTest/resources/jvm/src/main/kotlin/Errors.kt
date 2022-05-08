package jvm.src.main.kotlin

import kotlin.system.exitProcess

class Errors {
    fun kotlinExit() {
        exitProcess(0)
    }

    fun javaExit() {
        @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
        System.exit(1)
    }
}
