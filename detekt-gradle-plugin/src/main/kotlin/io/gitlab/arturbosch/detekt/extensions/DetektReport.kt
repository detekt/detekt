package io.gitlab.arturbosch.detekt.extensions

import java.io.File

class DetektReport(val name: String) {

	var enabled: Boolean = DetektExtension.DEFAULT_REPORT_ENABLED_VALUE

	var destination: File? = null

	override fun toString(): String {
		return "DetektReport(name='$name', enabled=$enabled, destination=$destination)"
	}


}
