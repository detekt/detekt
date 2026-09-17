package dev.detekt.api.internal

import dev.detekt.detekt_api.BuildConfig

/**
 * Returns the version of Kotlin that detekt was compiled with
 */
fun whichKotlin(): String = BuildConfig.KOTLIN_IMPLEMENTATION_VERSION

/**
 * Returns the bundled detekt version.
 */
fun whichDetekt(): String = BuildConfig.DETEKT_VERSION
