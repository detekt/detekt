package io.gitlab.arturbosch.detekt.core

import java.net.URL
import java.util.jar.Manifest

fun whichOS(): String = System.getProperty("os.name")

fun whichJava(): String = System.getProperty("java.runtime.version")

fun whichDetekt(): String? {
    fun readVersion(resource: URL): String? = resource.openStream()
        .use { Manifest(it).mainAttributes.getValue("DetektVersion") }

    return Detektor::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
        .asSequence()
        .mapNotNull { runCatching { readVersion(it) }.getOrNull() }
        .firstOrNull()
}
