package io.gitlab.arturbosch.detekt

import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val detektExtension = project.extensions.create("detekt", DetektExtension::class.java)
		detektExtension.input = project.projectDir.absolutePath

		project.task(mapOf("type" to DetektCheckTask::class.java), "detektCheck")
		project.task(mapOf("type" to DetektFormatTask::class.java), "detektFormat")
		project.task(mapOf("type" to DetektMigrateTask::class.java), "detektMigrate")
		project.task(mapOf("type" to DetektIdeaFormatTask::class.java), "detektIdeaFormat")
		project.task(mapOf("type" to DetektIdeaInspectionTask::class.java), "detektIdeaInspect")
	}

}
