package io.gitlab.arturbosch.detekt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.JavaExec

class DetektPlugin : Plugin<Project> {

	private val formatString = "--format"
	private val disableDefaults = "--disableDefaultRuleSets"

	override fun apply(project: Project) {
		val detektConfig = project.extensions.create("detekt", DetektConfig::class.java)

		val configuration = project.configurations.maybeCreate("detekt")
		project.dependencies.add(configuration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektConfig.version))

		val formatting = project.configurations.maybeCreate("detektFormat")
		project.dependencies.add(formatting.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektConfig.version))
		project.dependencies.add(formatting.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-formatting", detektConfig.version))

		val migration = project.configurations.maybeCreate("detektMigrate")
		project.dependencies.add(migration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-migration", detektConfig.version))

		detektConfig.input = project.projectDir.absolutePath

		project.afterEvaluate {

			val args = detektConfig.configure()
			if (detektConfig.debug) println(args)

			project.tasks.create("detekt", JavaExec::class.java) {
				it.description = "Analyze your kotlin code with detekt."
				it.main = "io.gitlab.arturbosch.detekt.cli.Main"
				it.classpath = configuration
				it.args(args)
			}

			project.tasks.create("detektFormat", JavaExec::class.java) {
				it.description = "Format your kotlin code with detekt."
				it.main = "io.gitlab.arturbosch.detekt.cli.Main"
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

	private fun DetektConfig.configure(): MutableList<String> {
		val args = mutableListOf<String>()
		input?.let { args.add("--project"); args.add(it) }
		config?.let { args.add("--config"); args.add(it) }
		configResource?.let { args.add("--config-resource"); args.add(it) }
		filters?.let { args.add("--filters"); args.add(it) }
		rulesets?.let { args.add("--rules"); args.add(it) }
		report?.let { args.add("--report"); args.add(it) }
		output?.let { args.add("--output") }
		baseline?.let { args.add("--baseline") }
		parallel?.let { args.add("--parallel") }
		format?.let { args.add("--format") }
		useTabs?.let { args.add("--useTabs") }
		disableDefaultRuleSets?.let { args.add("--disable") }
		return args
	}

}

@Suppress("LongParameterList")
open class DetektConfig(var version: String = "1.0.0.M10",
						var input: String? = null,
						var config: String? = null,
						var configResource: String? = null,
						var filters: String? = null,
						var rulesets: String? = null,
						var report: String? = null,
						var output: String? = null,
						var baseline: String? = null,
						var parallel: String? = null,
						var format: String? = null,
						var useTabs: String? = null,
						var disableDefaultRuleSets: String? = null,
						var debug: Boolean = false)
