package io.gitlab.arturbosch.detekt.core

import java.io.IOException
import java.net.URL
import java.util.jar.Manifest

/**
 * @author Artur Bosch
 */

fun whichOS(): String = System.getProperty("os.name")

fun whichJava(): String = System.getProperty("java.runtime.version")

fun whichDetekt(): String {
	for (resource in Detektor::class.java.classLoader.getResources("META-INF/MANIFEST.MF")) {
		try {
			val version = readDetektVersionInManifest(resource)
			if (version != null) {
				return version
			}
		} catch (_: IOException) {
			// we search for the manifest with the detekt version
		}
	}
	return "unknown"
}

private fun readDetektVersionInManifest(resource: URL) =
		resource.openStream().use {
			Manifest().mainAttributes
					.getValue("DetektVersion")
		}
