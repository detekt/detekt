package io.gitlab.arturbosch.detekt.sample.extensions.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun qualifiedNamesReport(detektion: Detektion): String? {
	// referencing the original key 'fqNamesKey' does not retrieve the stored values
	// using the deprecated method seems to work for unknown reasons
	val key = Key.findKeyByName("FQNames") as Key<Set<String>>
	val fqNames = detektion.getData(key)
	println("fqNames: " + fqNames)
	if (fqNames == null || fqNames.isEmpty()) return null

	return with(StringBuilder()) {
		fqNames.forEach {
			append(it + "\n")
		}
		toString()
	}
}
