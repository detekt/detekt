package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File

class DetektReport(val type: DetektReportType,
				   private val project: Project) {

	private var reportName: String = DEFAULT_FILENAME

	var enabled: Boolean? = null

	var destination: File? = null

	fun setReportName(reportName: String) {
		val customDestination = destination
		if (customDestination != null && reportName != DEFAULT_FILENAME) {
			val parent = customDestination.parent
			destination = File(parent, "$reportName.${type.extension}")
		} else {
			this.reportName = reportName
		}
	}

	override fun toString(): String {
		return "DetektReport(type='$type', enabled=$enabled, destination=$destination)"
	}

	fun getTargetFileProvider(reportsDir: Provider<File>): Provider<RegularFile> {
		return project.provider {
			if (enabled ?: DetektExtension.DEFAULT_REPORT_ENABLED_VALUE) {
				getTargetFile(reportsDir.get())
			} else {
				null
			}
		}
	}

	private fun getTargetFile(reportsDir: File): RegularFile {
		val prop = project.layout.fileProperty()
		val customDestination = destination
		if (customDestination != null) {
			prop.set(customDestination)
		} else {
			val name = "$reportName.${type.extension}"
			prop.set(File(reportsDir, name))
		}
		return prop.get()
	}

	companion object {
		const val DEFAULT_FILENAME = "detekt"
	}
}
