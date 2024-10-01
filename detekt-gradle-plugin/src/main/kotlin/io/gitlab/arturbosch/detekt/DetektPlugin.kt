package io.gitlab.arturbosch.detekt

import dev.detekt.gradle.plugin.CONFIGURATION_DETEKT_PLUGINS
import dev.detekt.gradle.plugin.DetektBasePlugin
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.CONFIG_DIR_NAME
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.CONFIG_FILE
import dev.detekt.gradle.plugin.internal.DetektAndroidCompilations
import dev.detekt.gradle.plugin.internal.DetektJvmCompilations
import dev.detekt.gradle.plugin.internal.DetektKmpJvmCompilations
import dev.detekt.gradle.plugin.internal.conventionCompat
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.DetektPlain
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import java.net.URL
import java.util.jar.Manifest

class DetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(DetektBasePlugin::class.java)

        val extension = project.extensions.getByType(DetektExtension::class.java)

        configurePluginDependencies(project, extension)
        setTaskDefaults(project, extension)

        project.registerDetektPlainTask(extension)
        project.registerDetektJvmTasks(extension)
        val enableAndroidTasks =
            !project.providers
                .gradleProperty(DETEKT_ANDROID_DISABLED_PROPERTY)
                .getOrElse("false")
                .toBoolean()
        if (enableAndroidTasks) {
            project.registerDetektAndroidTasks(extension)
        }
        val enableMppTasks =
            !project.providers
                .gradleProperty(DETEKT_MULTIPLATFORM_DISABLED_PROPERTY)
                .getOrElse("false")
                .toBoolean()
        if (enableMppTasks) {
            project.registerDetektMultiplatformTasks(extension)
        }
        project.registerGenerateConfigTask(extension)
    }

    private fun Project.registerDetektJvmTasks(extension: DetektExtension) {
        plugins.withId("org.jetbrains.kotlin.jvm") {
            DetektJvmCompilations.registerTasks(project, extension)
        }
    }

    private fun Project.registerDetektMultiplatformTasks(extension: DetektExtension) {
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            DetektKmpJvmCompilations.registerTasks(project, extension)
        }
    }

    private fun Project.registerDetektAndroidTasks(extension: DetektExtension) {
        plugins.withId("kotlin-android") {
            DetektAndroidCompilations.registerTasks(project, extension)
            DetektAndroidCompilations.linkTasks(project, extension)
        }
    }

    private fun Project.registerDetektPlainTask(extension: DetektExtension) {
        DetektPlain(this).registerTasks(extension)
    }

    private fun Project.registerGenerateConfigTask(extension: DetektExtension) {
        val detektGenerateConfigSingleExecution = project.gradle.sharedServices.registerIfAbsent(
            "DetektGenerateConfigSingleExecution",
            DetektGenerateConfigTask.SingleExecutionBuildService::class.java
        ) { spec ->
            spec.maxParallelUsages.set(1)
        }

        tasks.register(GENERATE_CONFIG, DetektGenerateConfigTask::class.java) {
            it.configFile.convention {
                extension.config.lastOrNull() ?: project.file("$rootDir/$CONFIG_DIR_NAME/$CONFIG_FILE")
            }
            it.usesService(detektGenerateConfigSingleExecution)
        }
    }

    private fun configurePluginDependencies(project: Project, extension: DetektExtension) {
        project.configurations.create(CONFIGURATION_DETEKT) { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "The $CONFIGURATION_DETEKT dependencies to be used for this project."
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false

            configuration.defaultDependencies { dependencySet ->
                val version = extension.toolVersion.get()
                dependencySet.add(project.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:$version"))
            }
        }
    }

    private fun setTaskDefaults(project: Project, extension: DetektExtension) {
        project.tasks.withType(Detekt::class.java).configureEach { task ->
            task.detektClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT))
            task.pluginClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT_PLUGINS))
            val reportName = if (task.name.startsWith(DETEKT_TASK_NAME) && task.name != DETEKT_TASK_NAME) {
                task.name.removePrefix(DETEKT_TASK_NAME).decapitalize()
            } else {
                task.name
            }
            task.reports.html { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("$reportName.html"))
            }
            task.reports.md { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("$reportName.md"))
            }
            task.reports.sarif { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("$reportName.sarif"))
            }
            task.reports.xml { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("$reportName.xml"))
            }
        }

        project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach { task ->
            task.detektClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT))
            task.pluginClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT_PLUGINS))
        }

        project.tasks.withType(DetektGenerateConfigTask::class.java).configureEach { task ->
            task.detektClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT))
            task.pluginClasspath.conventionCompat(project.configurations.named(CONFIGURATION_DETEKT_PLUGINS))
        }
    }

    internal companion object {
        internal const val DETEKT_TASK_NAME = "detekt"
        internal const val BASELINE_TASK_NAME = "detektBaseline"
        private const val GENERATE_CONFIG = "detektGenerateConfig"
        val defaultExcludes = listOf("build/")
        val defaultIncludes = listOf("**/*.kt", "**/*.kts")

        internal const val DETEKT_ANDROID_DISABLED_PROPERTY = "detekt.android.disabled"
        internal const val DETEKT_MULTIPLATFORM_DISABLED_PROPERTY = "detekt.multiplatform.disabled"

        internal const val DEFAULT_REPORT_ENABLED_VALUE = true
    }
}

internal const val CONFIGURATION_DETEKT = "detekt"

internal fun ProviderFactory.isWorkerApiEnabled(): Boolean =
    gradleProperty("detekt.use.worker.api").getOrElse("false") == "true"

@Incubating
fun getSupportedKotlinVersion(): String =
    DetektPlugin::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
        .asSequence()
        .mapNotNull { runCatching { readVersion(it) }.getOrNull() }
        .first()

private fun readVersion(resource: URL): String? = resource.openConnection()
    .apply { useCaches = false }
    .getInputStream()
    .use { Manifest(it).mainAttributes.getValue("KotlinImplementationVersion") }
