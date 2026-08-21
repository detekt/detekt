package dev.detekt.core

val NL: String = System.lineSeparator()

/**
 * Returns the name of the running OS.
 */
fun whichOS(): String = System.getProperty("os.name")

/**
 * Returns the version of the running JVM.
 */
fun whichJava(): String = System.getProperty("java.runtime.version")
