package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileStorage
import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val profilesContainer = project.container(ProfileExtension::class.java)
		project.extensions.add(PROFILES_EXTENSION_NAME, profilesContainer)
		profilesContainer.all { ProfileStorage.add(it) }

		project.extensions.create(DETEKT_EXTENSION_NAME, DetektExtension::class.java)
		project.task(mapOf(TYPE to DetektCheckTask::class.java), CHECK)
		project.task(mapOf(TYPE to DetektIdeaFormatTask::class.java), IDEA_FORMAT)
		project.task(mapOf(TYPE to DetektIdeaInspectionTask::class.java), IDEA_INSPECT)
		project.task(mapOf(TYPE to DetektGenerateConfigTask::class.java), GENERATE_CONFIG)
		project.task(mapOf(TYPE to DetektCreateBaselineTask::class.java), BASELINE)
	}

	companion object {
		private const val DETEKT_EXTENSION_NAME = "detekt"
		private const val PROFILES_EXTENSION_NAME = "profiles"
		private const val TYPE = "type"
		private const val CHECK = "detektCheck"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"
	}
}
