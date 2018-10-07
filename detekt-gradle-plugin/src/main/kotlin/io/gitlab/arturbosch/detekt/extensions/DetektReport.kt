package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File

class DetektReport(val name: String, private val project: Project) {

	var enabled: Boolean? = null

	var destination: File? = null

	private val reportFileExtension
		get() = name

	override fun toString(): String {
		return "DetektReport(name='$name', enabled=$enabled, destination=$destination)"
	}

	fun getTargetFileProvider(reportsDir: Provider<Directory>): Provider<RegularFile> {
		return project.provider {
			if (enabled ?: DetektExtension.DEFAULT_REPORT_ENABLED_VALUE)
				getTargetFile(reportsDir.get())
			else
				null
		}
	}

	private fun getTargetFile(reportsDir: Directory): RegularFile {
		val prop = project.layout.fileProperty()
		val customDestination = destination
		if (customDestination != null)
			prop.set(customDestination)
		else
			prop.set(File(reportsDir.asFile, "$DEFAULT_FILENAME.$reportFileExtension"))

		return prop.get()
	}

	companion object {
		const val DEFAULT_FILENAME = "detekt"
	}
}
