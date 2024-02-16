package io.gitlab.arturbosch.detekt

import dev.detekt.gradle.plugin.CONFIGURATION_DETEKT_PLUGINS
import dev.detekt.gradle.plugin.DetektBasePlugin
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.CONFIG_DIR_NAME
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.DetektAndroid
import io.gitlab.arturbosch.detekt.internal.DetektJvm
import io.gitlab.arturbosch.detekt.internal.DetektMultiplatform
import io.gitlab.arturbosch.detekt.internal.DetektPlain
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        if (project.findProperty(DETEKT_ANDROID_DISABLED_PROPERTY) != "true") {
            project.registerDetektAndroidTasks(extension)
        }
        if (project.findProperty(DETEKT_MULTIPLATFORM_DISABLED_PROPERTY) != "true") {
            project.registerDetektMultiplatformTasks(extension)
        }
        project.registerGenerateConfigTask(extension)
    }

    private fun Project.registerDetektJvmTasks(extension: DetektExtension) {
        plugins.withId("org.jetbrains.kotlin.jvm") {
            DetektJvm(this).registerTasks(extension)
        }
    }

    private fun Project.registerDetektMultiplatformTasks(extension: DetektExtension) {
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            DetektMultiplatform(this).registerTasks(extension)
        }
    }

    private fun Project.registerDetektAndroidTasks(extension: DetektExtension) {
        plugins.withId("kotlin-android") {
            DetektAndroid(this).registerTasks(extension)
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
            task.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
            task.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
            task.reports.html { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("${task.name}.html"))
            }
            task.reports.md { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("${task.name}.md"))
            }
            task.reports.sarif { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("${task.name}.sarif"))
            }
            task.reports.txt { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("${task.name}.txt"))
            }
            task.reports.xml { report ->
                report.required.convention(DEFAULT_REPORT_ENABLED_VALUE)
                report.outputLocation.convention(extension.reportsDir.file("${task.name}.xml"))
            }
        }

        project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
            it.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
            it.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
        }

        project.tasks.withType(DetektGenerateConfigTask::class.java).configureEach {
            it.detektClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT))
            it.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
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
internal const val USE_WORKER_API = "detekt.use.worker.api"

@Incubating
fun getSupportedKotlinVersion(): String {
    return DetektPlugin::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
        .asSequence()
        .mapNotNull { runCatching { readVersion(it) }.getOrNull() }
        .first()
}

private fun readVersion(resource: URL): String? = resource.openConnection()
    .apply { useCaches = false }
    .getInputStream()
    .use { Manifest(it).mainAttributes.getValue("KotlinImplementationVersion") }
