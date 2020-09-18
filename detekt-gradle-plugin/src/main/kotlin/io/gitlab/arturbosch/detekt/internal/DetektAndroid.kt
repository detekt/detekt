package io.gitlab.arturbosch.detekt.internal

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.TestedVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal class DetektAndroid(private val project: Project) {

    private val mainTaskProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Main") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Run detekt analysis for production classes across " +
                    "all variants with type resolution"
        }
    }

    private val testTaskProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Test") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Run detekt analysis for test classes across " +
                    "all variants with type resolution"
        }
    }

    private val mainBaselineTaskProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Main") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Creates detekt baseline files for production classes across " +
                    "all variants with type resolution"
        }
    }

    private val testBaselineTaskProvider: TaskProvider<Task> by lazy {
        project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Test") {
            it.group = "verification"
            it.description = "EXPERIMENTAL: Creates detekt baseline files for test classes across " +
                    "all variants with type resolution"
        }
    }

    private val BaseExtension.variants: DomainObjectSet<out BaseVariant>?
        get() = when (this) {
            is AppExtension -> applicationVariants
            is LibraryExtension -> libraryVariants
            is TestExtension -> applicationVariants
            else -> null
        }

    private val BaseVariant.testVariants: List<BaseVariant>
        get() = if (this is TestedVariant) listOfNotNull(testVariant, unitTestVariant)
        else emptyList()

    fun registerDetektAndroidTasks(extension: DetektExtension) {
        // There is not a single Android plugin, but each registers an extension based on BaseExtension,
        // so we catch them all by looking for this one
        project.afterEvaluate {
            val baseExtension = project.extensions.findByType(BaseExtension::class.java)
            baseExtension?.let {
                val bootClasspath = project.files(baseExtension.bootClasspath)
                baseExtension.variants
                    ?.matching { !extension.matchesIgnoredConfiguration(it) }
                    ?.all { variant ->
                        project.registerAndroidDetektTask(bootClasspath, extension, variant).also { provider ->
                            mainTaskProvider.dependsOn(provider)
                        }
                        project.registerAndroidCreateBaselineTask(bootClasspath, extension, variant)
                            .also { provider ->
                                mainBaselineTaskProvider.dependsOn(provider)
                            }
                        variant.testVariants
                            .filter { !extension.matchesIgnoredConfiguration(it) }
                            .forEach { testVariant ->
                                project.registerAndroidDetektTask(bootClasspath, extension, testVariant)
                                    .also { provider ->
                                        testTaskProvider.dependsOn(provider)
                                    }
                                project.registerAndroidCreateBaselineTask(bootClasspath, extension, testVariant)
                                    .also { provider ->
                                        testBaselineTaskProvider.dependsOn(provider)
                                    }
                            }
                    }
            }
        }
    }

    private fun DetektExtension.matchesIgnoredConfiguration(variant: BaseVariant): Boolean =
        ignoredVariants.contains(variant.name) ||
                ignoredBuildTypes.contains(variant.buildType.name) ||
                ignoredFlavors.contains(variant.flavorName)

    private fun Project.registerAndroidDetektTask(
        bootClasspath: FileCollection,
        extension: DetektExtension,
        variant: BaseVariant
    ): TaskProvider<Detekt> =
        registerDetektTask(DetektPlugin.DETEKT_TASK_NAME + variant.name.capitalize(), extension) {
            setSource(variant.sourceSets.map { it.javaDirectories })
            classpath.setFrom(variant.getCompileClasspath(null).filter { it.exists() } + bootClasspath)
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            extension.baseline?.existingVariantOrBaseFile(variant.name)?.let { baselineFile ->
                baseline.set(layout.file(project.provider { baselineFile }))
            }
            reports.xml.setDefaultIfUnset(File(extension.reportsDir, variant.name + ".xml"))
            reports.html.setDefaultIfUnset(File(extension.reportsDir, variant.name + ".html"))
            reports.txt.setDefaultIfUnset(File(extension.reportsDir, variant.name + ".txt"))
            description = "EXPERIMENTAL: Run detekt analysis for ${variant.name} classes with type resolution"
        }

    private fun Project.registerAndroidCreateBaselineTask(
        bootClasspath: FileCollection,
        extension: DetektExtension,
        variant: BaseVariant
    ): TaskProvider<DetektCreateBaselineTask> =
        registerCreateBaselineTask(DetektPlugin.BASELINE_TASK_NAME + variant.name.capitalize(), extension) {
            setSource(variant.sourceSets.map { it.javaDirectories })
            classpath.setFrom(variant.getCompileClasspath(null).filter { it.exists() } + bootClasspath)
            val variantBaselineFile = extension.baseline?.addVariantName(variant.name)
            baseline.set(project.layout.file(project.provider { variantBaselineFile }))
            description = "EXPERIMENTAL: Creates detekt baseline for ${variant.name} classes with type resolution"
        }
}
