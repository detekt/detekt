package dev.detekt.api.internal

import dev.detekt.detekt_api.BuildConfig

/**
 * Returns the name of the running OS.
 */
fun whichOS(): String = System.getProperty("os.name")

/**
 * Returns the version of the running JVM.
 */
fun whichJava(): String = System.getProperty("java.runtime.version")

/**
 * Returns the version of Kotlin that detekt was compiled with
 */
fun whichKotlin(): String = BuildConfig.KOTLIN_IMPLEMENTATION_VERSION

/**
 * Returns the bundled detekt version.
 */
fun whichDetekt(): String = BuildConfig.DETEKT_VERSION
