package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileExtension
import io.gitlab.arturbosch.detekt.extensions.ProfileStorage
import org.gradle.api.Plugin
import org.gradle.api.Project


class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val profilesContainer = project.container(ProfileExtension::class.java)
		project.extensions.add("profiles", profilesContainer)
		profilesContainer.all { ProfileStorage.add(it) }

		project.extensions.create("detekt", DetektExtension::class.java)
		project.task(mapOf("type" to DetektCheckTask::class.java), "detektCheck")
		project.task(mapOf("type" to DetektIdeaFormatTask::class.java), "detektIdeaFormat")
		project.task(mapOf("type" to DetektIdeaInspectionTask::class.java), "detektIdeaInspect")
		project.task(mapOf("type" to DetektGenerateConfigTask::class.java), "detektGenerateConfig")
		project.task(mapOf("type" to DetektCreateBaselineTask::class.java), "detektBaseline")
	}

}
