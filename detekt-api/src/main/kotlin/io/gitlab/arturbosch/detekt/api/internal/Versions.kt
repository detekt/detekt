package io.gitlab.arturbosch.detekt.api.internal

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
 * Returns the bundled detekt version.
 */
fun whichDetekt(): String? {
    fun readVersion(resource: URL): String? = resource.openStream()
        .use { Manifest(it).mainAttributes.getValue("DetektVersion") }

    return Extension::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
        .asSequence()
        .mapNotNull { runCatching { readVersion(it) }.getOrNull() }
        .firstOrNull()
}
