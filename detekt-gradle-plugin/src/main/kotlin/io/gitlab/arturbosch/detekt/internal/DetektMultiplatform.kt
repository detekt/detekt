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
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.js
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.native
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import java.io.File

internal class DetektMultiplatform(private val project: Project) {

    fun registerTasks(extension: DetektExtension) {
        project.afterEvaluate {
            project.registerMultiplatformTasks(extension)
        }
    }

    private fun Project.registerMultiplatformTasks(extension: DetektExtension) {
        // We need another project.afterEvaluate as the Android target is attached on
        // a project.afterEvaluate inside AGP. We should further investigate and potentially remove this.
        project.afterEvaluate { evaluatedProject ->
            val kmpExtension = evaluatedProject.extensions.getByType(KotlinMultiplatformExtension::class.java)

            kmpExtension.targets.all { target ->
                target.compilations.forEach { compilation ->
                    val inputSource = compilation.kotlinSourceSets
                        .map { it.kotlin.sourceDirectories }
                        .fold(evaluatedProject.files() as FileCollection) { collection, next -> collection.plus(next) }

                    if (compilation is KotlinJvmAndroidCompilation) {
                        evaluatedProject.registerMultiplatformTasksForAndroidTarget(
                            compilation = compilation,
                            target = target,
                            extension = extension,
                            inputSource = inputSource
                        )
                    } else {
                        evaluatedProject.registerMultiplatformTasksForNonAndroidTarget(
                            compilation = compilation,
                            target = target,
                            extension = extension,
                            inputSource = inputSource
                        )
                    }
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
        val androidExtension = extensions.findByType(BaseExtension::class.java)
        androidExtension?.let {
            val bootClasspath = files(it.bootClasspath)
            val variant = compilation.androidVariant
            val detektTaskName = DetektPlugin.DETEKT_TASK_NAME +
                target.name.capitalize() + variant.name.capitalize()
            val baselineTaskName = DetektPlugin.BASELINE_TASK_NAME +
                target.name.capitalize() + variant.name.capitalize()
            registerAndroidDetektTask(
                bootClasspath,
                extension,
                compilation.androidVariant,
                detektTaskName,
                inputSource
            )
            registerAndroidCreateBaselineTask(
                bootClasspath,
                extension,
                compilation.androidVariant,
                baselineTaskName,
                inputSource
            )
        }
    }

    private fun Project.registerMultiplatformTasksForNonAndroidTarget(
        compilation: KotlinCompilation<KotlinCommonOptions>,
        target: KotlinTarget,
        extension: DetektExtension,
        inputSource: FileCollection
    ) {
        val taskSuffix = target.name.capitalize() + compilation.name.capitalize()
        val runWithTypeResolution = target.runWithTypeResolution

        registerDetektTask(
            DetektPlugin.DETEKT_TASK_NAME + taskSuffix,
            extension
        ) {
            setSource(inputSource)
            if (runWithTypeResolution) {
                classpath.setFrom(inputSource, compilation.compileDependencyFiles)
            }
            // If a baseline file is configured as input file, it must exist to be configured, otherwise the task fails.
            // We try to find the configured baseline or alternatively a specific variant matching this task.
            if (runWithTypeResolution) {
                extension.baseline?.existingVariantOrBaseFile(compilation.name)
            } else {
                extension.baseline?.takeIf { it.exists() }
            }?.let { baselineFile ->
                baseline.set(layout.file(provider { baselineFile }))
            }
            reports = extension.reports
            reports.xml.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".xml"))
            reports.html.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".html"))
            reports.txt.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".txt"))
            reports.sarif.setDefaultIfUnset(File(extension.reportsDir, compilation.name + ".sarif"))
            description =
                "Run detekt analysis for target ${target.name} and source set ${compilation.name}"
            if (runWithTypeResolution) {
                description = "EXPERIMENTAL: $description with type resolution."
            }
        }

        registerCreateBaselineTask(
            DetektPlugin.BASELINE_TASK_NAME + taskSuffix, extension
        ) {
            setSource(inputSource)
            if (runWithTypeResolution) {
                classpath.setFrom(inputSource, compilation.compileDependencyFiles)
            }
            val variantBaselineFile = if (runWithTypeResolution) {
                extension.baseline?.addVariantName(compilation.name)
            } else {
                extension.baseline
            }
            baseline.set(
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
    get() = when (platformType) {
        jvm, androidJvm -> true
        common, js, native -> false
    }
