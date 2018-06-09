package io.gitlab.arturbosch.detekt.core

import java.io.IOException
import java.util.jar.Manifest

/**
 * @author Artur Bosch
 */

fun whichOS(): String = System.getProperty("os.name")

fun whichJava(): String = System.getProperty("java.runtime.version")

fun whichDetekt(): String {
	for (resource in Detektor::class.java.classLoader
			.getResources("META-INF/MANIFEST.MF")) {
		try {
			Manifest(resource.openStream())
					.mainAttributes
					.getValue("DetektVersion")
					?.let { return it }
		} catch (_: IOException) {
			// we search for the manifest with the detekt version
		}
	}
	return "unknown"
}
