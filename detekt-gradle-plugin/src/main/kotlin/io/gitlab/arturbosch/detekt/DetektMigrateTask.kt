package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektMigrateTask : DefaultTask() {

	init {
		description = "Migrate your kotlin code with detekt."
	}

	@TaskAction
	fun migrate() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		val migration = project.buildscript.configurations.maybeCreate("detektMigrate")
		project.buildscript.dependencies.add(migration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-migration", detektExtension.version))

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.migration.Migration"
			it.classpath = migration
			it.args(detektExtension.profileArgumentsOrDefault(project))
		}
	}

}