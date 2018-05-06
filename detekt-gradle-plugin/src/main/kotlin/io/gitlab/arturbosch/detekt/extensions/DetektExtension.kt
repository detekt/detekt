package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.provider.Property
import org.gradle.api.resources.TextResource
import org.gradle.kotlin.dsl.property
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension
constructor(
		private val project: Project,
		projectLayout: ProjectLayout
) : CodeQualityExtension() {
	private val debugProperty: Property<Boolean?> = project.objects.property()
	private val parallelProperty: Property<Boolean?> = project.objects.property()
	private val disableDefaultRuleSetsProperty: Property<Boolean?> = project.objects.property()
	private val filtersProperty: Property<String?> = project.objects.property()
	private val baselineProperty: RegularFileProperty = projectLayout.fileProperty()
	private val pluginsProperty: Property<String?> = project.objects.property()
	private val configProperty: Property<TextResource?> = project.objects.property()
	private val configDirProperty: RegularFileProperty = projectLayout.fileProperty()
	var ideaExtension: IdeaExtension = IdeaExtension()

	var debug: Boolean?
		get() = debugProperty.get()
		set(value) = debugProperty.set(value)

	var parallel: Boolean?
		get() = parallelProperty.get()
		set(value) = parallelProperty.set(value)

	var disableDefaultRuleSets: Boolean?
		get() = disableDefaultRuleSetsProperty.get()
		set(value) = disableDefaultRuleSetsProperty.set(value)

	var filters: String?
		get() = filtersProperty.get()
		set(value) {
			println("Filters $value")
			filtersProperty.set(value)
		}

	var plugins: String?
		get() = pluginsProperty.get()
		set(value) = pluginsProperty.set(value)

	var baseline: RegularFile
		get() = baselineProperty.get()
		set(value) = baselineProperty.set(value)

	var configDir: RegularFile
		get() = configDirProperty.get()
		set(value) = configDirProperty.set(value)

	var config: TextResource?
		get() = configProperty.get()
		set(value) = configProperty.set(value)

	var configFile: File?
		get() = configProperty.get()?.asFile()
		set(value) = configProperty.set(project.resources.text.fromFile(configFile))

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}
}
