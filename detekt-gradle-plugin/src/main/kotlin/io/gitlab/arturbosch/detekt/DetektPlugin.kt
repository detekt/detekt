package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.loadDetektVersion
import io.gitlab.arturbosch.detekt.internal.DetektAndroid
import io.gitlab.arturbosch.detekt.internal.DetektJvm
import io.gitlab.arturbosch.detekt.internal.DetektMultiplatform
import io.gitlab.arturbosch.detekt.internal.DetektPlain
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension

class DetektPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)
        val extension =
            project.extensions.findByType(DetektExtension::class.java) ?: project.extensions.create(
                DETEKT_EXTENSION,
                DetektExtension::class.java
            )

        with(extension) {
            toolVersion.convention(loadDetektVersion(DetektExtension::class.java.classLoader))
            ignoreFailures.convention(DEFAULT_IGNORE_FAILURES)
            source.setFrom(
                DEFAULT_SRC_DIR_JAVA,
                DEFAULT_TEST_SRC_DIR_JAVA,
                DEFAULT_SRC_DIR_KOTLIN,
                DEFAULT_TEST_SRC_DIR_KOTLIN,
            )
            baseline.convention(project.layout.projectDirectory.file("detekt-baseline.xml"))
            enableCompilerPlugin.convention(DEFAULT_COMPILER_PLUGIN_ENABLED)
            debug.convention(DEFAULT_DEBUG_VALUE)
            parallel.convention(DEFAULT_PARALLEL_VALUE)
            allRules.convention(DEFAULT_ALL_RULES_VALUE)
            buildUponDefaultConfig.convention(DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE)
            disableDefaultRuleSets.convention(DEFAULT_DISABLE_RULESETS_VALUE)
            autoCorrect.convention(DEFAULT_AUTO_CORRECT_VALUE)
            reportsDir.convention(
                project.extensions.getByType(ReportingExtension::class.java).baseDirectory.dir("detekt")
            )
            basePath.convention(project.rootProject.layout.projectDirectory)
        }

        val defaultConfigFile =
            project.file("${project.rootProject.layout.projectDirectory.dir(CONFIG_DIR_NAME)}/$CONFIG_FILE")
        if (defaultConfigFile.exists()) {
            extension.config.setFrom(project.files(defaultConfigFile))
        }

        configurePluginDependencies(project, extension)
        setTaskDefaults(project)

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
                val version = extension.toolVersion.get()
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

        const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        const val DEFAULT_TEST_SRC_DIR_JAVA = "src/test/java"
        const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        const val DEFAULT_TEST_SRC_DIR_KOTLIN = "src/test/kotlin"
        const val DEFAULT_DEBUG_VALUE = false
        const val DEFAULT_IGNORE_FAILURES = false
        const val DEFAULT_PARALLEL_VALUE = false
        const val DEFAULT_AUTO_CORRECT_VALUE = false
        const val DEFAULT_DISABLE_RULESETS_VALUE = false
        const val DEFAULT_REPORT_ENABLED_VALUE = true
        const val DEFAULT_ALL_RULES_VALUE = false
        const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false

        // This flag is ignored unless the compiler plugin is applied to the project
        const val DEFAULT_COMPILER_PLUGIN_ENABLED = true
    }
}

const val CONFIGURATION_DETEKT = "detekt"
const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
const val USE_WORKER_API = "detekt.use.worker.api"
