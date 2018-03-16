package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
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
		objectFactory: ObjectFactory,
		projectLayout: ProjectLayout
) : CodeQualityExtension() {
	var debug: Property<Boolean> = objectFactory.property()
	var parallel: Property<Boolean> = objectFactory.property()
	var disableDefaultRuleSets: Property<Boolean> = objectFactory.property()
	var filters: Property<String> = objectFactory.property()
	var baseline: RegularFileProperty = projectLayout.fileProperty()
	var plugins: Property<String> = objectFactory.property()
	var config: Property<TextResource> = objectFactory.property()
	var configDir: RegularFileProperty = projectLayout.fileProperty()
	var ideaExtension: IdeaExtension = IdeaExtension()

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	/**
	 * The Detekt configuration file to use.
	 */
	fun getConfigFile(): File? {
		return config.get().asFile()
	}

	/**
	 * The Detekt configuration file to use.
	 */
	fun setConfigFile(configFile: File) {
		config.set(project.resources.text.fromFile(configFile))
	}
}
