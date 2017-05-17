package io.gitlab.arturbosch.detekt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.JavaExec

class DetektPlugin : Plugin<Project> {

	private val formatString = "--format"
	private val disableDefaults = "--disableDefaultRuleSets"

	override fun apply(project: Project) {
		val detektExtension = project.extensions.create("detekt", DetektExtension::class.java)
		detektExtension.input = project.projectDir.absolutePath

		project.afterEvaluate {

			val configuration = project.buildscript.configurations.maybeCreate("detekt")
			project.buildscript.dependencies.add(configuration.name, DefaultExternalModuleDependency(
					"io.gitlab.arturbosch.detekt", "detekt-cli", detektExtension.version))

			val formatting = project.buildscript.configurations.maybeCreate("detektFormat")
			project.buildscript.dependencies.add(formatting.name, DefaultExternalModuleDependency(
					"io.gitlab.arturbosch.detekt", "detekt-formatting", detektExtension.version))

			val migration = project.buildscript.configurations.maybeCreate("detektMigrate")
			project.buildscript.dependencies.add(migration.name, DefaultExternalModuleDependency(
					"io.gitlab.arturbosch.detekt", "detekt-migration", detektExtension.version))

			val args = detektExtension.convertToArguments()
			if (detektExtension.debug) println("detekt version: ${detektExtension.version}: " + args)

			project.tasks.create("detekt", JavaExec::class.java) {
				it.description = "Analyze your kotlin code with detekt."
				it.main = "io.gitlab.arturbosch.detekt.cli.Main"
				it.classpath = configuration
				it.args(args)
			}

			project.tasks.create("detektFormat", JavaExec::class.java) {
				it.description = "Format your kotlin code with detekt."
				it.main = "io.gitlab.arturbosch.detekt.formatting.Formatting"
				it.classpath = formatting
				it.args(args.plus(listOf(formatString, disableDefaults)))
			}

			project.tasks.create("detektMigrate", JavaExec::class.java) {
				it.description = "Migrate your kotlin code with detekt."
				it.main = "io.gitlab.arturbosch.detekt.migration.Migration"
				it.classpath = migration
				it.args(args)
			}
		}

	}

}
