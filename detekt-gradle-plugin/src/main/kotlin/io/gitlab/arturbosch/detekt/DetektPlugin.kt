package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.DetektAndroid
import io.gitlab.arturbosch.detekt.internal.DetektJvm
import io.gitlab.arturbosch.detekt.internal.DetektMultiplatform
import io.gitlab.arturbosch.detekt.internal.DetektPlain
import io.gitlab.arturbosch.detekt.internal.gradlePropertyAtConfigTimeCompat
import io.gitlab.arturbosch.detekt.internal.rootProjectDirectoryCompat
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import java.net.URL
import java.util.jar.Manifest

class DetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)
        val extension =
            project.extensions.findByType(DetektExtension::class.java) ?: project.extensions.create(
                DETEKT_EXTENSION,
                DetektExtension::class.java
            )

        extension.reportsDir = project.extensions.getByType(ReportingExtension::class.java).file("detekt")

        val defaultConfigFile =
            project.file("${project.rootProjectDirectoryCompat().dir(CONFIG_DIR_NAME)}/$CONFIG_FILE")
        if (defaultConfigFile.exists()) {
            extension.config.setFrom(project.files(defaultConfigFile))
        }

        configurePluginDependencies(project, extension)
        setTaskDefaults(project)

        project.registerDetektPlainTask(extension)
        project.registerDetektJvmTasks(extension)
        val enableAndroidTasks =
            !project.providers
                .gradlePropertyAtConfigTimeCompat(DETEKT_ANDROID_DISABLED_PROPERTY)
                .getOrElse("false")
                .toBoolean()
        if (enableAndroidTasks) {
            project.registerDetektAndroidTasks(extension)
        }
        val enableMppTasks =
            !project.providers
                .gradlePropertyAtConfigTimeCompat(DETEKT_MULTIPLATFORM_DISABLED_PROPERTY)
                .getOrElse("false")
                .toBoolean()
        if (enableMppTasks) {
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
            @Suppress("DEPRECATION") // Internal usage, might be rewired later.
            it.config.setFrom(project.provider { extension.config })
            it.usesService(detektGenerateConfigSingleExecution)
        }
    }

    private fun configurePluginDependencies(project: Project, extension: DetektExtension) {
        project.configurations.maybeCreate(CONFIGURATION_DETEKT_PLUGINS).let { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false
        }

        project.configurations.create(CONFIGURATION_DETEKT) { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "The $CONFIGURATION_DETEKT dependencies to be used for this project."
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false

            configuration.defaultDependencies { dependencySet ->
                val version = extension.toolVersion
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
            it.pluginClasspath.setFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
        }
    }

    companion object {
        const val DETEKT_TASK_NAME = "detekt"
        const val BASELINE_TASK_NAME = "detektBaseline"
        const val DETEKT_EXTENSION = "detekt"
        private const val GENERATE_CONFIG = "detektGenerateConfig"
        internal val defaultExcludes = listOf("build/")
        internal val defaultIncludes = listOf("**/*.kt", "**/*.kts")
        internal const val CONFIG_DIR_NAME = "config/detekt"
        internal const val CONFIG_FILE = "detekt.yml"

        internal const val DETEKT_ANDROID_DISABLED_PROPERTY = "detekt.android.disabled"
        internal const val DETEKT_MULTIPLATFORM_DISABLED_PROPERTY = "detekt.multiplatform.disabled"
    }
}

const val CONFIGURATION_DETEKT = "detekt"
const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
const val USE_WORKER_API = "detekt.use.worker.api"

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
