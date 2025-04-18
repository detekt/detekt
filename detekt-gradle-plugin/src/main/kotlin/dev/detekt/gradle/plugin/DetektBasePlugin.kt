package dev.detekt.gradle.plugin

import dev.detekt.gradle.plugin.internal.mapExplicitArgMode
import dev.detekt.gradle.plugin.internal.rootProjectDirectoryCompat
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.FailOnSeverity
import io.gitlab.arturbosch.detekt.extensions.loadDetektVersion
import io.gitlab.arturbosch.detekt.internal.addVariantName
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import io.gitlab.arturbosch.detekt.internal.setCreateBaselineTaskDefaults
import io.gitlab.arturbosch.detekt.internal.setDetektTaskDefaults
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
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
            autoCorrect.convention(
                project.providers.gradleProperty(ENABLE_AUTOCORRECT)
                    .map { it.toBoolean() }
                    .orElse(DEFAULT_AUTO_CORRECT_VALUE)
            )
            reportsDir.convention(
                project.extensions.getByType(ReportingExtension::class.java).baseDirectory.dir("detekt")
            )
            basePath.convention(project.rootProjectDirectoryCompat())
        }

        val defaultConfigFile =
            project.file("${project.rootProjectDirectoryCompat().dir(CONFIG_DIR_NAME)}/$CONFIG_FILE")
        if (defaultConfigFile.exists()) {
            extension.config.setFrom(project.files(defaultConfigFile))
        }

        project.configurations.create(CONFIGURATION_DETEKT_PLUGINS) { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false
        }

        project.setTaskDefaults(extension)
        project.registerSourceSetTasks(extension)
    }

    private fun Project.setTaskDefaults(extension: DetektExtension) {
        setDetektTaskDefaults(extension)
        setCreateBaselineTaskDefaults(extension)
    }

    private fun Project.registerSourceSetTasks(extension: DetektExtension) {
        project.plugins.withType(KotlinBasePlugin::class.java) {
            project.extensions.getByType(KotlinSourceSetContainer::class.java)
                .sourceSets
                .all { sourceSet ->
                    val taskName = "${DetektPlugin.DETEKT_TASK_NAME}${sourceSet.name.capitalize()}SourceSet"
                    tasks.register(taskName, Detekt::class.java) { detektTask ->
                        detektTask.source = sourceSet.kotlin
                        detektTask.baseline.convention(
                            project.layout.file(
                                extension.baseline.flatMap {
                                    providers.provider {
                                        it.asFile.existingVariantOrBaseFile("${sourceSet.name}SourceSet")
                                    }
                                }
                            )
                        )
                        if (sourceSet.name == "main") {
                            detektTask.explicitApi.convention(mapExplicitArgMode())
                        }
                        detektTask.description = "Run detekt analysis for ${sourceSet.name} source set"
                    }

                    val baseLineTaskName = "${DetektPlugin.BASELINE_TASK_NAME}${sourceSet.name.capitalize()}SourceSet"
                    tasks.register(baseLineTaskName, DetektCreateBaselineTask::class.java) { createBaselineTask ->
                        createBaselineTask.source = sourceSet.kotlin

                        createBaselineTask.baseline.convention(
                            project.layout.file(
                                extension.baseline.flatMap {
                                    providers.provider { it.asFile.addVariantName("${sourceSet.name}SourceSet") }
                                }
                            )
                        )

                        if (sourceSet.name == "main") {
                            createBaselineTask.explicitApi.convention(mapExplicitArgMode())
                        }
                        createBaselineTask.description = "Creates detekt baseline for ${sourceSet.name} source set"
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
internal const val ENABLE_AUTOCORRECT = "detekt.default.autocorrect"
