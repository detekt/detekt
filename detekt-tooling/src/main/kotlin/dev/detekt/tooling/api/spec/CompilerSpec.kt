package dev.detekt.tooling.api.spec

import java.nio.file.Path

/**
 * All these properties are based down to the Kotlin compiler for type- and symbol resolution.
 */
interface CompilerSpec {

    /**
     * Target version for the generated JVM bytecode (e.g. 1.8, 9, 10, 11 ...).
     */
    val jvmTarget: String

    /**
     * Kotlin language version (e.g. 1.0, 1.1, 1.2, 1.3 ...).
     */
    val languageVersion: String?

    /**
     * Kotlin API version (e.g. 1.0, 1.1, 1.2, 1.3 ...).
     */
    val apiVersion: String?

    /**
     * Paths to class files and jars separated by a path separator.
     */
    val classpath: String?

    /**
     * Path to custom JDK home. Includes the custom JDK from the specified location into the classpath instead of using
     * the JRE from the runtime environment.
     */
    val jdkHome: Path?

    /**
     * Options to pass to the Kotlin compiler.
     */
    val freeCompilerArgs: List<String>
}
