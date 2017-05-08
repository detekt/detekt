package io.gitlab.arturbosch.detekt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.JavaExec

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val detektConfig = project.extensions.create("detekt", DetektConfig::class.java)
		val configuration = project.configurations.maybeCreate("detekt")

		project.dependencies.add(configuration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektConfig.version))

		detektConfig.input = project.projectDir.absolutePath

		project.afterEvaluate {

			val args = mutableListOf<String>()

			with(detektConfig) {
				input?.let { args.add("--project"); args.add(it) }
				config?.let { args.add("--config"); args.add(it) }
				configResource?.let { args.add("--config-resource"); args.add(it) }
				filters?.let { args.add("--filters"); args.add(it) }
				rulesets?.let { args.add("--rules"); args.add(it) }
				report?.let { args.add("--report"); args.add(it) }
				output?.let { args.add("--output"); args.add(it) }
				baseline?.let { args.add("--baseline"); args.add(it) }
				parallel?.let { args.add("--parallel"); args.add(it) }
				format?.let { args.add("--format"); args.add(it) }
				useTabs?.let { args.add("--useTabs"); args.add(it) }
			}

			project.tasks.create("detekt", JavaExec::class.java) {
				it.description = "Analyze your kotlin code with detekt!"
				it.main = "io.gitlab.arturbosch.detekt.cli.Main"
				it.classpath = configuration
				it.args(args)
			}
		}

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
						var useTabs: String? = null)
