package dev.detekt.gradle.plugin.internal

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Component
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal object DetektAndroidCompilations {
    fun registerTasks(project: Project, extension: DetektExtension) {
        val kotlinAndroid = project.extensions.getByType(KotlinAndroidExtension::class.java)
        project.extensions.findByType(AndroidComponentsExtension::class.java)?.onVariants { variant ->
            variant.registerDetektTasks(project, extension, kotlinAndroid)

            @Suppress("UnstableApiUsage")
            variant.nestedComponents.forEach { nested ->
                nested.registerDetektTasks(project, extension, kotlinAndroid)
            }
        }
    }

    private fun Component.registerDetektTasks(
        project: Project,
        extension: DetektExtension,
        kotlinAndroid: KotlinAndroidExtension,
    ) {
        val kotlin = sources.kotlin?.all ?: return
        val source = project.objects.fileCollection().from(kotlin)
        val componentArtifacts = artifacts
        kotlinAndroid.target.compilations.matching { it.name == name }
            .configureEach { compilation ->
                val detektTask = project.registerJvmCompilationDetektTask(
                    extension = extension,
                    compilation = compilation,
                    source = source,
                )
                val baselineTask = project.registerJvmCompilationCreateBaselineTask(
                    extension = extension,
                    compilation = compilation,
                    source = source,
                )
                // Put this component's compiled classes (including classes compiled from generated
                // Java sources such as BuildConfig and view binding) on detekt's type-resolution
                // classpath. Built-in Kotlin excludes generated sources from the analyzed source set,
                // so without their classes type resolution fails on those symbols. ScopedArtifact.CLASSES
                // (PROJECT scope) also wires the compile dependency.
                componentArtifacts.forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(detektTask)
                    .toGet(ScopedArtifact.CLASSES, { it.generatedClassesJars }, { it.generatedClassesDirs })
                componentArtifacts.forScope(ScopedArtifacts.Scope.PROJECT)
                    .use(baselineTask)
                    .toGet(ScopedArtifact.CLASSES, { it.generatedClassesJars }, { it.generatedClassesDirs })
            }
    }

    private fun DetektExtension.matchesIgnoredConfiguration(variant: Variant): Boolean =
        ignoredVariants.get().contains(variant.name) ||
            ignoredBuildTypes.get().contains(variant.buildType) ||
            ignoredFlavors.get().contains(variant.flavorName)

    fun linkTasks(project: Project, extension: DetektExtension) {
        val mainTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "Run detekt analysis for production classes across " +
                    "all variants with type resolution"
            }

        val testTaskProvider =
            project.tasks.register("${DetektPlugin.DETEKT_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "Run detekt analysis for test classes across " +
                    "all variants with type resolution"
            }

        val mainBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Main") {
                it.group = "verification"
                it.description = "Creates detekt baseline files for production classes across " +
                    "all variants with type resolution"
            }

        val testBaselineTaskProvider =
            project.tasks.register("${DetektPlugin.BASELINE_TASK_NAME}Test") {
                it.group = "verification"
                it.description = "Creates detekt baseline files for test classes across " +
                    "all variants with type resolution"
            }

        project.extensions.findByType(AndroidComponentsExtension::class.java)?.let { componentsExtension ->
            componentsExtension.onVariants { variant ->
                if (!extension.matchesIgnoredConfiguration(variant)) {
                    mainTaskProvider.configure { task ->
                        task.dependsOn(DetektPlugin.DETEKT_TASK_NAME + variant.name.replaceFirstChar { it.uppercase() })
                    }
                    mainBaselineTaskProvider.configure { task ->
                        task.dependsOn(
                            DetektPlugin.BASELINE_TASK_NAME + variant.name.replaceFirstChar { it.uppercase() }
                        )
                    }
                    variant.nestedComponents.forEach { testVariant ->
                        testTaskProvider.configure { task ->
                            task.dependsOn(
                                DetektPlugin.DETEKT_TASK_NAME + testVariant.name.replaceFirstChar { it.uppercase() }
                            )
                        }
                        testBaselineTaskProvider.configure { task ->
                            task.dependsOn(
                                DetektPlugin.BASELINE_TASK_NAME + testVariant.name.replaceFirstChar { it.uppercase() }
                            )
                        }
                    }
                }
            }
        }
    }
}
