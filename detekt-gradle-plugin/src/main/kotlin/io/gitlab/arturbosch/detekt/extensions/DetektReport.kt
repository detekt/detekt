package io.gitlab.arturbosch.detekt.extensions

import io.gitlab.arturbosch.detekt.internal.fileProperty
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File

class DetektReport(val type: DetektReportType, private val project: Project) {

	var enabled: Boolean? = null

	var destination: File? = null

	override fun toString(): String {
		return "DetektReport(type='$type', enabled=$enabled, destination=$destination)"
	}

	fun getTargetFileProvider(reportsDir: Provider<File>): Provider<RegularFile> {
		return project.provider {
			if (enabled ?: DetektExtension.DEFAULT_REPORT_ENABLED_VALUE)
				getTargetFile(reportsDir.get())
			else
				null
		}
	}

	private fun getTargetFile(reportsDir: File): RegularFile {
		val prop = project.fileProperty()
		val customDestination = destination
		if (customDestination != null)
			prop.set(customDestination)
		else
			prop.set(File(reportsDir, "$DEFAULT_FILENAME.${type.extension}"))

		return prop.get()
	}

	companion object {
		const val DEFAULT_FILENAME = "detekt"
	}
}
