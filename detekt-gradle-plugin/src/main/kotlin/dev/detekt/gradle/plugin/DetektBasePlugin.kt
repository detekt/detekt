package dev.detekt.gradle.plugin

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.FailOnSeverity
import io.gitlab.arturbosch.detekt.extensions.loadDetektVersion
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import io.gitlab.arturbosch.detekt.internal.registerDetektTask
import io.gitlab.arturbosch.detekt.internal.setReportOutputConventions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

class DetektBasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)

        val extension = project.extensions.create(DETEKT_EXTENSION, DetektExtension::class.java)

        with(extension) {
            toolVersion.convention(loadDetektVersion(DetektExtension::class.java.classLoader))
            ignoreFailures.convention(DEFAULT_IGNORE_FAILURES)
            failOnSeverity.convention(DEFAULT_FAIL_ON_SEVERITY)
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

        project.configurations.create(CONFIGURATION_DETEKT_PLUGINS).let { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false
        }

        project.registerSourceSetTasks(extension)
    }

    private fun Project.registerSourceSetTasks(extension: DetektExtension) {
        project.plugins.withType(KotlinBasePlugin::class.java) {
            project.extensions.getByType(KotlinSourceSetContainer::class.java)
                .sourceSets
                .withType(KotlinSourceSet::class.java) { sourceSet ->
                    val taskName = "${DetektPlugin.DETEKT_TASK_NAME}${sourceSet.name.capitalize()}SourceSet"
                    project.registerDetektTask(taskName, extension) {
                        source = sourceSet.kotlin
                        // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
                        // We try to find the configured baseline or alternatively a specific variant matching this task.
                        extension.baseline.asFile.orNull?.existingVariantOrBaseFile("${sourceSet.name}SourceSet")
                            ?.let { file ->
                                baseline.convention(project.layout.file(project.provider { file }))
                            }
                        setReportOutputConventions(reports, extension, sourceSet.name)
                        description = "Run detekt analysis for ${sourceSet.name} source set"
                    }
                }
        }
    }

    internal companion object {
        internal const val DETEKT_EXTENSION = "detekt"
        internal const val CONFIG_DIR_NAME = "config/detekt"
        internal const val CONFIG_FILE = "detekt.yml"

        private const val DEFAULT_SRC_DIR_JAVA = "src/main/java"
        private const val DEFAULT_TEST_SRC_DIR_JAVA = "src/test/java"
        private const val DEFAULT_SRC_DIR_KOTLIN = "src/main/kotlin"
        private const val DEFAULT_TEST_SRC_DIR_KOTLIN = "src/test/kotlin"
        private const val DEFAULT_DEBUG_VALUE = false
        private const val DEFAULT_IGNORE_FAILURES = false
        private val DEFAULT_FAIL_ON_SEVERITY = FailOnSeverity.Error
        private const val DEFAULT_PARALLEL_VALUE = false
        private const val DEFAULT_AUTO_CORRECT_VALUE = false
        private const val DEFAULT_DISABLE_RULESETS_VALUE = false
        private const val DEFAULT_ALL_RULES_VALUE = false
        private const val DEFAULT_BUILD_UPON_DEFAULT_CONFIG_VALUE = false

        // This flag is ignored unless the compiler plugin is applied to the project
        private const val DEFAULT_COMPILER_PLUGIN_ENABLED = true
    }
}

internal const val CONFIGURATION_DETEKT_PLUGINS = "detektPlugins"
