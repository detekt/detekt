package io.gitlab.arturbosch.detekt

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.SourceKind
import com.android.build.gradle.internal.api.TestedVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

@Suppress("TooManyFunctions")
class DetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)
        val extension = project.extensions.create(DETEKT_TASK_NAME, DetektExtension::class.java, project)
        extension.reportsDir = project.extensions.getByType(ReportingExtension::class.java).file("detekt")

        val defaultConfigFile =
            project.file("${project.rootProject.layout.projectDirectory.dir(CONFIG_DIR_NAME)}/$CONFIG_FILE")
        if (defaultConfigFile.exists()) {
            extension.config = project.files(defaultConfigFile)
        }

        configurePluginDependencies(project, extension)
        setTaskDefaults(project)

        project.registerOldDetektTask(extension)
        project.registerDetektTasks(extension)
        project.registerCreateBaselineTask(extension)
        project.registerGenerateConfigTask()
    }

    private fun Project.registerDetektTasks(extension: DetektExtension) {
        // There is not a single Android plugin, but each registers an extension based on BaseExtension,
        // so we catch them all by looking for this one
        project.afterEvaluate {
            val androidExtension = project.extensions.findByType(BaseExtension::class.java)
            androidExtension?.let {
                val mainTaskProvider = project.tasks.register("${DETEKT_TASK_NAME}Main") {
                    it.group = "verification"
                    it.description = "EXPERIMENTAL & SLOW: Run detekt analysis for production classes across " +
                            "all variants with type resolution"
                }
                val testTaskProvider = project.tasks.register("${DETEKT_TASK_NAME}Test") {
                    it.group = "verification"
                    it.description = "EXPERIMENTAL & SLOW: Run detekt analysis for test classes across " +
                            "all variants with type resolution"
                }
                androidExtension.variants?.all { variant ->
                    project.registerAndroidDetektTask(extension, variant).also { provider ->
                        mainTaskProvider.dependsOn(provider)
                    }
                    variant.testVariants.forEach { testVariant ->
                        project.registerAndroidDetektTask(extension, testVariant).also { provider ->
                            testTaskProvider.dependsOn(provider)
                        }
                    }
                }
            }
        }

        // Kotlin JVM plugin
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.afterEvaluate {
                project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.all { sourceSet ->
                    project.registerJvmDetektTask(extension, sourceSet)
                }
            }
        }
    }

    private val BaseExtension.variants: DomainObjectSet<out BaseVariant>?
        get() = when (this) {
            is AppExtension -> applicationVariants
            is LibraryExtension -> libraryVariants
            is TestedExtension -> testVariants
            else -> null
        }

    private val BaseVariant.testVariants: List<BaseVariant>
        get() = if (this is TestedVariant) listOfNotNull(testVariant, unitTestVariant)
        else emptyList()

    private fun Project.registerOldDetektTask(extension: DetektExtension) {
        val detektTaskProvider = tasks.register(DETEKT_TASK_NAME, Detekt::class.java) {
            it.debugProp.set(project.provider { extension.debug })
            it.parallelProp.set(project.provider { extension.parallel })
            it.disableDefaultRuleSetsProp.set(project.provider { extension.disableDefaultRuleSets })
            it.buildUponDefaultConfigProp.set(project.provider { extension.buildUponDefaultConfig })
            it.failFastProp.set(project.provider { extension.failFast })
            it.autoCorrectProp.set(project.provider { extension.autoCorrect })
            it.config.setFrom(project.provider { extension.config })
            it.baseline.set(project.layout.file(project.provider { extension.baseline }))
            it.setSource(existingInputDirectoriesProvider(project, extension))
            it.setIncludes(defaultIncludes)
            it.setExcludes(defaultExcludes)
            it.reportsDir.set(project.provider { extension.customReportsDir })
            it.reports = extension.reports
            it.ignoreFailuresProp.set(project.provider { extension.ignoreFailures })
        }

        tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
            it.dependsOn(detektTaskProvider)
        }
    }

    private fun Project.registerAndroidDetektTask(
        extension: DetektExtension,
        baseVariant: BaseVariant
    ): TaskProvider<Detekt> =
        registerDetektTask(DETEKT_TASK_NAME + baseVariant.name.capitalize(), extension) {
            it.setSource(baseVariant.getSourceFolders(SourceKind.JAVA))
            it.classpath.setFrom(baseVariant.getCompileClasspath(null))
            it.reports.xml.destination = File(extension.reportsDir, baseVariant.name + ".xml")
            it.reports.html.destination = File(extension.reportsDir, baseVariant.name + ".html")
            it.reports.txt.destination = File(extension.reportsDir, baseVariant.name + ".txt")
            it.description =
                "EXPERIMENTAL & SLOW: Run detekt analysis for ${baseVariant.name} classes with type resolution"
        }

    private fun Project.registerJvmDetektTask(extension: DetektExtension, sourceSet: SourceSet) {
        val kotlinSourceSet = (sourceSet as HasConvention).convention.plugins["kotlin"] as? KotlinSourceSet
            ?: throw GradleException("Kotlin source set not found. Please report on detekt's issue tracker")
        registerDetektTask(DETEKT_TASK_NAME + sourceSet.name.capitalize(), extension) {
            it.setSource(kotlinSourceSet.kotlin.files)
            it.classpath.setFrom(sourceSet.compileClasspath, sourceSet.output.classesDirs)
            it.reports.xml.destination = File(extension.reportsDir, sourceSet.name + ".xml")
            it.reports.html.destination = File(extension.reportsDir, sourceSet.name + ".html")
            it.reports.txt.destination = File(extension.reportsDir, sourceSet.name + ".txt")
            it.description =
                "EXPERIMENTAL & SLOW: Run detekt analysis for ${sourceSet.name} classes with type resolution"
        }
    }

    private fun Project.registerDetektTask(
        name: String,
        extension: DetektExtension,
        configuration: (Detekt) -> Unit
    ): TaskProvider<Detekt> =
        tasks.register(name, Detekt::class.java) {
            it.debugProp.set(provider { extension.debug })
            it.parallelProp.set(provider { extension.parallel })
            it.disableDefaultRuleSetsProp.set(provider { extension.disableDefaultRuleSets })
            it.buildUponDefaultConfigProp.set(provider { extension.buildUponDefaultConfig })
            it.failFastProp.set(provider { extension.failFast })
            it.autoCorrectProp.set(provider { extension.autoCorrect })
            it.config.setFrom(provider { extension.config })
            it.baseline.set(layout.file(project.provider { extension.baseline }))
            it.ignoreFailuresProp.set(project.provider { extension.ignoreFailures })
            configuration(it)
        }

    private fun Project.registerCreateBaselineTask(extension: DetektExtension) =
        tasks.register(BASELINE, DetektCreateBaselineTask::class.java) {
            it.baseline.set(project.layout.file(project.provider { extension.baseline }))
            it.config.setFrom(project.provider { extension.config })
            it.debug.set(project.provider { extension.debug })
            it.parallel.set(project.provider { extension.parallel })
            it.disableDefaultRuleSets.set(project.provider { extension.disableDefaultRuleSets })
            it.buildUponDefaultConfig.set(project.provider { extension.buildUponDefaultConfig })
            it.failFast.set(project.provider { extension.failFast })
            it.autoCorrect.set(project.provider { extension.autoCorrect })
            it.setSource(existingInputDirectoriesProvider(project, extension))
            it.setIncludes(defaultIncludes)
            it.setExcludes(defaultExcludes)
        }

    private fun Project.registerGenerateConfigTask() =
        tasks.register(GENERATE_CONFIG, DetektGenerateConfigTask::class.java)

    private fun existingInputDirectoriesProvider(
        project: Project,
        extension: DetektExtension
    ): Provider<FileCollection> = project.provider { extension.input.filter { it.exists() } }

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

    private fun setTaskDefaults(project: Project) {
        project.tasks.withType(Detekt::class.java).configureEach {
            it.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
            it.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
        }

        project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
            it.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
            it.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
        }

        project.tasks.withType(DetektGenerateConfigTask::class.java).configureEach {
            it.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
        }
    }

    companion object {
        private const val DETEKT_TASK_NAME = "detekt"
        private const val GENERATE_CONFIG = "detektGenerateConfig"
        private const val BASELINE = "detektBaseline"
        private val defaultExcludes = listOf("build/")
        private val defaultIncludes = listOf("**/*.kt", "**/*.kts")
        internal const val CONFIG_DIR_NAME = "config/detekt"
        internal const val CONFIG_FILE = "detekt.yml"
    }
}

const val CONFIGURATION_DETEKT = "detekt"
const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
