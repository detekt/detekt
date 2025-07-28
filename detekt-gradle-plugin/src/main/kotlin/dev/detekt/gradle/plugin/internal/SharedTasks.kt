package dev.detekt.gradle.plugin.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.internal.addVariantName
import io.gitlab.arturbosch.detekt.internal.existingVariantOrBaseFile
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.registerJvmCompilationDetektTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<*>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    tasks.register(DetektPlugin.DETEKT_TASK_NAME + taskSuffix.capitalize(), Detekt::class.java) { detektTask ->
        val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

        detektTask.setSource(siblingTask.sources)
        detektTask.classpath.conventionCompat(compilation.output.classesDirs, siblingTask.libraries)
        detektTask.friendPaths.conventionCompat(siblingTask.friendPaths)
        detektTask.apiVersion.convention(siblingTask.compilerOptions.apiVersion.map { it.version })
        detektTask.languageVersion.convention(siblingTask.compilerOptions.languageVersion.map { it.version })
        /* Note: jvmTarget convention is also set in setDetektTaskDefaults. There may be a race between setting it here
           as well, but they should both set the same value. This should possibly be revisited in the future. */
        detektTask.jvmTarget.convention(siblingTask.compilerOptions.jvmTarget.map { it.target })
        detektTask.freeCompilerArgs.convention(siblingTask.compilerOptions.freeCompilerArgs)
        detektTask.optIn.convention(siblingTask.compilerOptions.optIn)
        detektTask.noJdk.convention(siblingTask.compilerOptions.noJdk)
        detektTask.multiPlatformEnabled.convention(siblingTask.multiPlatformEnabled)
        if (compilation.name == "main") {
            detektTask.explicitApi.convention(mapExplicitArgMode())
        }

        detektTask.baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.existingVariantOrBaseFile(compilation.name) }
                }
            )
        )
        detektTask.description = if (target != null) {
            "Run detekt analysis for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "Run detekt analysis for ${compilation.name} classes with type resolution"
        }
    }
}

internal fun Project.registerJvmCompilationCreateBaselineTask(
    extension: DetektExtension,
    compilation: KotlinCompilation<*>,
    target: KotlinTarget? = null,
) {
    val taskSuffix = if (target != null) compilation.name + target.name.capitalize() else compilation.name
    tasks.register(
        DetektPlugin.BASELINE_TASK_NAME + taskSuffix.capitalize(),
        DetektCreateBaselineTask::class.java,
    ) { createBaselineTask ->
        val siblingTask = compilation.compileTaskProvider.get() as KotlinJvmCompile

        createBaselineTask.setSource(siblingTask.sources)
        createBaselineTask.classpath.conventionCompat(compilation.output.classesDirs, siblingTask.libraries)
        createBaselineTask.friendPaths.conventionCompat(siblingTask.friendPaths)
        createBaselineTask.apiVersion.convention(siblingTask.compilerOptions.apiVersion.map { it.version })
        createBaselineTask.languageVersion.convention(siblingTask.compilerOptions.languageVersion.map { it.version })
        /* Note: jvmTarget convention is also set in setCreateBaselineTaskDefaults. There may be a race between setting
           it here as well, but they should both set the same value. This should possibly be revisited in the future. */
        createBaselineTask.jvmTarget.convention(siblingTask.compilerOptions.jvmTarget.map { it.target })
        createBaselineTask.freeCompilerArgs.convention(siblingTask.compilerOptions.freeCompilerArgs)
        createBaselineTask.optIn.convention(siblingTask.compilerOptions.optIn)
        createBaselineTask.noJdk.convention(siblingTask.compilerOptions.noJdk)
        createBaselineTask.multiPlatformEnabled.convention(siblingTask.multiPlatformEnabled)
        if (compilation.name == "main") {
            createBaselineTask.explicitApi.convention(mapExplicitArgMode())
        }

        createBaselineTask.baseline.convention(
            project.layout.file(
                extension.baseline.flatMap {
                    providers.provider { it.asFile.addVariantName(compilation.name) }
                }
            )
        )
        createBaselineTask.description = if (target != null) {
            "Creates detekt baseline for compilation ${compilation.name} on target " +
                "${compilation.target.name} with type resolution"
        } else {
            "Creates detekt baseline for ${compilation.name} classes with type resolution"
        }
    }
}

internal fun Project.mapExplicitArgMode(): Provider<String> =
    provider {
        when (kotlinExtension.explicitApi) {
            ExplicitApiMode.Strict -> "strict"
            ExplicitApiMode.Warning -> "warning"
            else -> null
        }
    }

internal fun Project.setDetektTaskDefaults(extension: DetektExtension) {
    tasks.withType(Detekt::class.java) {
        project.plugins.withType(JavaBasePlugin::class.java) { _ ->
            val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

            // acquire a provider that returns the launcher for the toolchain
            val service = project.extensions.getByType(JavaToolchainService::class.java)
            val defaultLauncher = service.launcherFor(toolchain)
            it.jdkHome.convention(defaultLauncher.map { launcher -> launcher.metadata.installationPath })
            it.jvmTarget.convention(
                defaultLauncher.map { launcher ->
                    JavaVersion.toVersion(launcher.metadata.languageVersion.asInt()).toString()
                }
            )
        }

        it.debug.convention(extension.debug)
        it.parallel.convention(extension.parallel)
        it.disableDefaultRuleSets.convention(extension.disableDefaultRuleSets)
        it.buildUponDefaultConfig.convention(extension.buildUponDefaultConfig)
        it.autoCorrect.convention(extension.autoCorrect)
        it.ignoreFailures.convention(extension.ignoreFailures)
        it.failOnSeverity.convention(extension.failOnSeverity)
        it.config.conventionCompat(provider { extension.config })
        it.basePath.convention(extension.basePath.map { basePath -> basePath.asFile.absolutePath })
        it.allRules.convention(extension.allRules)
        it.noJdk.convention(false)
        it.multiPlatformEnabled.convention(false)
    }
}

internal fun Project.setCreateBaselineTaskDefaults(extension: DetektExtension) {
    tasks.withType(DetektCreateBaselineTask::class.java) {
        project.plugins.withType(JavaBasePlugin::class.java) { _ ->
            val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

            // acquire a provider that returns the launcher for the toolchain
            val service = project.extensions.getByType(JavaToolchainService::class.java)
            val defaultLauncher = service.launcherFor(toolchain)
            it.jdkHome.convention(defaultLauncher.map { launcher -> launcher.metadata.installationPath })
            it.jvmTarget.convention(
                defaultLauncher.map { launcher ->
                    JavaVersion.toVersion(launcher.metadata.languageVersion.asInt()).toString()
                }
            )
        }

        it.config.conventionCompat(project.provider { extension.config })
        it.debug.convention(extension.debug)
        it.parallel.convention(extension.parallel)
        it.disableDefaultRuleSets.convention(extension.disableDefaultRuleSets)
        it.buildUponDefaultConfig.convention(extension.buildUponDefaultConfig)
        it.autoCorrect.convention(extension.autoCorrect)
        it.ignoreFailures.convention(extension.ignoreFailures)
        it.basePath.convention(extension.basePath.map { basePath -> basePath.asFile.absolutePath })
        it.allRules.convention(extension.allRules)
        it.noJdk.convention(false)
        it.multiPlatformEnabled.convention(false)
    }
}
