package io.github.detekt.tooling.api.spec

import org.jetbrains.kotlin.config.JvmTarget

/**
 * All these properties are based down to the Kotlin compiler for type- and symbol resolution.
 */
interface CompilerSpec {

    /**
     * Target version for the generated JVM bytecode (e.g. 1.8, 9, 10, 11 ...).
     */
    val jvmTarget: JvmTarget

    /**
     * Kotlin language version (e.g. 1.0, 1.1, 1.2, 1.3 ...).
     */
    val languageVersion: String?

    /**
     * Paths to class files and jars separated by a path separator.
     */
    val classpath: String?
}
