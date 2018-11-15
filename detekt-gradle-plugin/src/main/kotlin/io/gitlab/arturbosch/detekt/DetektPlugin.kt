package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		val extension = project.extensions.create(DETEKT, DetektExtension::class.java, project)

		configurePluginDependencies(project, extension)

		createDetektTasks(project, extension)
		createAndConfigureCreateBaselineTask(project, extension)
		createAndConfigureGenerateConfigTask(project, extension)

		createAndConfigureIdeaTasks(project, extension)
	}

	private fun createDetektTasks(project: Project, extension: DetektExtension) {
		val sourceSets = mutableSetOf<String>()
		project.sourceSets?.forEach { sourceSet ->
			sourceSets += sourceSet.name
			val name = "$DETEKT${sourceSet.name.capitalize()}"
			val description = "Runs detekt on the ${sourceSet.name} source set."
			val inputProvider = project.provider { sourceSet.allSource.sourceDirectories.filter { it.exists() } }
			val sourceSetTask =
					createAndConfigureDetektTask(project,
							extension,
							name,
							description,
							inputProvider,
							sourceSet.compileClasspath)
			project.tasks.findByName(LifecycleBasePlugin.CHECK_TASK_NAME)?.dependsOn(sourceSetTask)
		}

		// TODO kotlin-multiplatform ios source sets are currently not considered
		setOf("kotlin", "kotlin-multiplatform", "kotlin-android", "kotlin2js")
				.forEach { pluginId ->
					project.plugins.withId(pluginId) {
						project.applyKotlinSourceSetDetektTasks(extension, sourceSets)
					}
				}


		val detektTask =
				createAndConfigureDetektTask(project,
						extension,
						DETEKT,
						"Runs the default detekt task.",
						existingInputDirectoriesProvider(project, extension))
		project.tasks.findByName(LifecycleBasePlugin.CHECK_TASK_NAME)?.dependsOn(detektTask)
	}

	private fun Project.applyKotlinSourceSetDetektTasks(extension: DetektExtension, existingSourceSets: MutableSet<String>) {
		project.kotlinSourceSets
				.filter { !existingSourceSets.contains(it.name) }
				.forEach { sourceSet ->
					existingSourceSets += sourceSet.name
					val name = "$DETEKT${sourceSet.name.capitalize()}"
					val description = "Runs detekt on the kotlin ${sourceSet.name} source set."
					val sourceDirectorySet = getKotlinSourceDirectorySetSafe(sourceSet)
					sourceDirectorySet?.let {
						val inputProvider = project.provider { it.sourceDirectories.filter { it.exists() } }
						// TODO How to provide Classpath for KotlinSourceSet?
						val sourceSetTask =
								createAndConfigureDetektTask(project,
										extension,
										name,
										description,
										inputProvider)
						project.tasks.findByName(LifecycleBasePlugin.CHECK_TASK_NAME)?.dependsOn(sourceSetTask)
					}
				}
	}

	private fun createAndConfigureDetektTask(project: Project,
											 extension: DetektExtension,
											 name: String,
											 taskDescription: String,
											 inputSources: Provider<FileCollection>,
											 compileClasspath: FileCollection = project.files()): TaskProvider<Detekt> {

		return project.tasks.register(name, Detekt::class.java) {
			it.description = taskDescription
			it.debugProp.set(project.provider { extension.debug })
			it.parallelProp.set(project.provider { extension.parallel })
			it.disableDefaultRuleSetsProp.set(project.provider { extension.disableDefaultRuleSets })
			it.filters.set(project.provider { extension.filters })
			it.config.setFrom(project.provider { extension.config })
			it.baseline.set(project.layout.file(project.provider { extension.baseline }))
			it.plugins.set(project.provider { extension.plugins })
			it.input.setFrom(project.provider { inputSources })
			it.classpath.setFrom(project.provider { compileClasspath })
			it.reportsDir.set(project.provider { extension.customReportsDir })
			// TODO this does not set the report name correctly
			it.reports = extension.reports.apply {
				xml.setReportName(name)
				html.setReportName(name)
			}
		}
	}

	private fun createAndConfigureCreateBaselineTask(project: Project, extension: DetektExtension) =
			project.tasks.register(BASELINE, DetektCreateBaselineTask::class.java) {
				it.baseline.set(project.layout.file(project.provider { extension.baseline }))
				it.config.setFrom(project.provider { extension.config })
				it.debug.set(project.provider { extension.debug })
				it.parallel.set(project.provider { extension.parallel })
				it.disableDefaultRuleSets.set(project.provider { extension.disableDefaultRuleSets })
				it.filters.set(project.provider { extension.filters })
				it.plugins.set(project.provider { extension.plugins })
				it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			}

	private fun createAndConfigureGenerateConfigTask(project: Project, extension: DetektExtension) =
			project.tasks.register(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) {
				it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			}

	private fun createAndConfigureIdeaTasks(project: Project, extension: DetektExtension) {
		project.tasks.register(IDEA_FORMAT, DetektIdeaFormatTask::class.java) {
			it.debug.set(project.provider { extension.debug })
			it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			it.ideaExtension = extension.idea
		}

		project.tasks.register(IDEA_INSPECT, DetektIdeaInspectionTask::class.java) {
			it.debug.set(project.provider { extension.debug })
			it.input.setFrom(existingInputDirectoriesProvider(project, extension))
			it.ideaExtension = extension.idea
		}
	}

	private fun existingInputDirectoriesProvider(project: Project, extension: DetektExtension): Provider<FileCollection> =
			project.provider { extension.input.filter { it.exists() } }

	private fun configurePluginDependencies(project: Project, extension: DetektExtension) {
		project.configurations.create(CONFIGURATION_DETEKT_PLUGINS) { configuration ->
			configuration.isVisible = false
			configuration.isTransitive = true
			configuration.description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
		}

		project.configurations.create(CONFIGURATION_DETEKT) { configuration ->
			configuration.isVisible = false
			configuration.isTransitive = true
			configuration.description = "The $CONFIGURATION_DETEKT dependencies to be used for this project."

			configuration.defaultDependencies { dependencySet ->
				@Suppress("USELESS_ELVIS")
				val version = extension.toolVersion ?: DEFAULT_DETEKT_VERSION
				dependencySet.add(project.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:$version"))
			}
		}
	}

	private val Project.sourceSets: SourceSetContainer?
		get() = project.extensions.findByType(SourceSetContainer::class.java)

	private val Project.kotlinSourceSets: NamedDomainObjectCollection<out Named>
		get() {
			// Access through reflection, because another project's KotlinProjectExtension might be loaded by a different class loader:
			val kotlinExt = project.extensions.getByName("kotlin")
			@Suppress("UNCHECKED_CAST")
			val sourceSets = kotlinExt.javaClass.getMethod("getSourceSets").invoke(kotlinExt) as NamedDomainObjectCollection<out Named>
			return sourceSets
		}

	private fun getKotlinSourceDirectorySetSafe(from: Any): SourceDirectorySet? {
		val getKotlin = from.javaClass.getMethod("getKotlin")
		return getKotlin(from) as? SourceDirectorySet
	}

	companion object {
		private const val DETEKT = "detekt"
		private const val IDEA_FORMAT = "detektIdeaFormat"
		private const val IDEA_INSPECT = "detektIdeaInspect"
		private const val GENERATE_CONFIG = "detektGenerateConfig"
		private const val BASELINE = "detektBaseline"
	}
}

const val CONFIGURATION_DETEKT = "detekt"
const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
