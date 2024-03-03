package io.gitlab.arturbosch.detekt.internal

import com.android.build.gradle.BaseExtension
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation

internal class DetektMultiplatform(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        project.registerMultiplatformTasks(extension)
    }

    private fun Project.registerMultiplatformTasks(extension: DetektExtension) {
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).targets.all { target ->
            target.compilations.all { compilation ->
                val inputSource = compilation.kotlinSourceSets
                    .map { it.kotlin.sourceDirectories }
                    .fold(project.files() as FileCollection) { collection, next -> collection.plus(next) }

                if (compilation is KotlinJvmAndroidCompilation) {
                    project.registerMultiplatformTasksForAndroidTarget(
                        compilation = compilation,
                        target = target,
                        extension = extension,
                        inputSource = inputSource
                    )
                } else {
                    project.registerMultiplatformTasksForNonAndroidTarget(
                        compilation = compilation,
                        target = target,
                        extension = extension,
                        inputSource = inputSource
                    )
                }
            }
        }
    }

    private fun Project.registerMultiplatformTasksForAndroidTarget(
        compilation: KotlinJvmAndroidCompilation,
        target: KotlinTarget,
        extension: DetektExtension,
        inputSource: FileCollection
    ) {
        // For Android targets we delegate to DetektAndroid as we need to access
        // BaseVariant and other AGP apis to properly setup the classpath.
        extensions.findByType(BaseExtension::class.java)?.let {
            val bootClasspath = files(provider { it.bootClasspath })
            val taskSuffix = compilation.name.capitalize() + target.name.capitalize()
            registerAndroidDetektTask(
                bootClasspath,
                extension,
                compilation.androidVariant,
                taskSuffix,
                inputSource
            )
            registerAndroidCreateBaselineTask(
                bootClasspath,
                extension,
                compilation.androidVariant,
                taskSuffix,
                inputSource
            )
        }
    }

    @Suppress("LongMethod")
    private fun Project.registerMultiplatformTasksForNonAndroidTarget(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        target: KotlinTarget,
        extension: DetektExtension,
        inputSource: FileCollection
    ) {
        val taskSuffix = compilation.name.capitalize() + target.name.capitalize()
        val runWithTypeResolution = target.runWithTypeResolution

        registerDetektTask(
            DetektPlugin.DETEKT_TASK_NAME + taskSuffix,
            extension
        ) {
            setSource(inputSource)
            if (runWithTypeResolution) {
                classpath.setFrom(compilation.output.classesDirs, compilation.compileDependencyFiles)
            }
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            if (runWithTypeResolution) {
                extension.baseline.asFile.orNull?.existingVariantOrBaseFile(compilation.name)
            } else {
                extension.baseline.asFile.orNull?.takeIf { it.exists() }
            }?.let { baselineFile ->
                baseline.convention(layout.file(provider { baselineFile }))
            }
            description =
                "Run detekt analysis for target ${target.name} and source set ${compilation.name}"
            if (runWithTypeResolution) {
                description = "EXPERIMENTAL: $description with type resolution."
            }
        }

        registerCreateBaselineTask(
            DetektPlugin.BASELINE_TASK_NAME + taskSuffix,
            extension
        ) {
            setSource(inputSource)
            if (runWithTypeResolution) {
                classpath.setFrom(compilation.output.classesDirs, compilation.compileDependencyFiles)
            }
            val variantBaselineFile = if (runWithTypeResolution) {
                extension.baseline.asFile.orNull?.addVariantName(compilation.name)
            } else {
                extension.baseline.get().asFile
            }
            baseline.convention(
                layout.file(provider { variantBaselineFile })
            )

            description =
                "Creates detekt baseline for ${target.name} and source set ${compilation.name}"
            if (runWithTypeResolution) {
                description = "EXPERIMENTAL: $description with type resolution."
            }
        }
    }
}

// We currently run type resolution only for Jvm & Android targets as
// native/js targets needs a different compiler classpath.
private val KotlinTarget.runWithTypeResolution: Boolean
    get() = platformType in setOf(jvm, androidJvm)
