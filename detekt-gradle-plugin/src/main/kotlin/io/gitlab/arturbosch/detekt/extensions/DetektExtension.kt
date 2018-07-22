package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.provider.Property
import org.gradle.api.reporting.ReportingExtension
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(project: Project) : CodeQualityExtension() {
	val defaultReportsDir = project.layout.buildDirectory.get()
			.dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
			.dir("detekt").asFile
	val defaultSourceDirectories = project.files("src/main/java", "src/main/kotlin")

	val reports = project.extensions.create("reports", DetektReportsExtension::class.java, project)
	val idea: IdeaExtension = project.extensions.create("idea", IdeaExtension::class.java)

	val inputProperty: Property<FileCollection> = project.objects.property(FileCollection::class.java)
	var input: FileCollection?
		get() = inputProperty.orNull
		set(value) = inputProperty.set(value)

	val baselineProperty: Property<File> = project.objects.property(File::class.java)
	var baseline: File?
		get() = baselineProperty.orNull
		set(value) = baselineProperty.set(value)

	val configProperty: Property<File> = project.objects.property(File::class.java)
	var config: File?
		get() = configProperty.orNull
		set(value) = configProperty.set(value)

	val debugProperty: Property<java.lang.Boolean> = project.objects.property(java.lang.Boolean::class.java)
	var debug: Boolean
		get() = debugProperty.orNull?.booleanValue() ?: DEFAULT_DEBUG_VALUE
		set(value) = debugProperty.set(java.lang.Boolean(value))

	val parallelProperty: Property<java.lang.Boolean> = project.objects.property(java.lang.Boolean::class.java)
	var parallel: Boolean
		get() = parallelProperty.orNull?.booleanValue() ?: DEFAULT_PARALLEL_VALUE
		set(value) = parallelProperty.set(java.lang.Boolean(value))

	val disableDefaultRuleSetsProperty: Property<java.lang.Boolean> = project.objects.property(java.lang.Boolean::class.java)
	var disableDefaultRuleSets: Boolean
		get() = disableDefaultRuleSetsProperty.orNull?.booleanValue() ?: DEFAULT_DISABLE_RULESETS_VALUE
		set(value) = disableDefaultRuleSetsProperty.set(java.lang.Boolean(value))

	var filtersProperty: Property<String> = project.objects.property(String::class.java)
	var filters: String?
		get() = filtersProperty.orNull
		set(value) = filtersProperty.set(value)

	var pluginsProperty: Property<String> = project.objects.property(String::class.java)
	var plugins: String?
		get() = pluginsProperty.orNull
		set(value) = pluginsProperty.set(value)
}

open class DetektReportsExtension(project: Project) {
	val xml = project.extensions.create("xml", DetektReportExtension::class.java, project)
	val html = project.extensions.create("html", DetektReportExtension::class.java, project)
	fun withName(name: String) = when (name.toLowerCase()) {
		"xml" -> xml
		"html" -> html
		else -> throw IllegalArgumentException("name '${name}' is not a supported report name")
	}
}

open class DetektReportExtension(project: Project) {

	open var enabled: Boolean = DEFAULT_REPORT_ENABLED_VALUE

	/**
	 * destination of the output - relative to the project root
	 */
	val destinationProperty: Property<File> = project.objects.property(File::class.java)
	var destination: File?
		get() = destinationProperty.orNull
		set(value) = destinationProperty.set(value)

}

