package io.gitlab.arturbosch.detekt.api.internal

import dev.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Extension
import java.net.URL
import java.util.jar.Manifest

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
fun whichKotlin(): String = getManifestValue("KotlinImplementationVersion")

/**
 * Returns the bundled detekt version.
 */
fun whichDetekt(): String = getManifestValue("DetektVersion")

private fun getManifestValue(key: String): String {
    fun readVersion(resource: URL): String? = resource.openSafeStream()
        .use { Manifest(it).mainAttributes.getValue(key) }

    return Extension::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
        .asSequence()
        .mapNotNull { runCatching { readVersion(it) }.getOrNull() }
        .first()
}
