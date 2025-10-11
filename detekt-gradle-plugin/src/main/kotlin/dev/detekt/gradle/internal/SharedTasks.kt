package dev.detekt.gradle.internal

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.internal.conventionCompat
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

internal fun Project.setDetektTaskDefaults(extension: DetektExtension) {
    tasks.withType(Detekt::class.java).configureEach {
        project.plugins.withType(JavaBasePlugin::class.java) { _ ->
            val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

            // acquire a provider that returns the launcher for the toolchain
            val service = project.extensions.getByType(JavaToolchainService::class.java)
            val defaultLauncher = service.launcherFor(toolchain)
            it.jdkHome.convention(defaultLauncher.map { launcher -> launcher.metadata.installationPath })
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") { _ ->
            val compilerOptions = project.extensions.getByType(KotlinJvmExtension::class.java).compilerOptions

            it.jvmTarget.convention(compilerOptions.jvmTarget.map { jvmTarget -> jvmTarget.target })
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
    tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
        project.plugins.withType(JavaBasePlugin::class.java) { _ ->
            val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

            // acquire a provider that returns the launcher for the toolchain
            val service = project.extensions.getByType(JavaToolchainService::class.java)
            val defaultLauncher = service.launcherFor(toolchain)
            it.jdkHome.convention(defaultLauncher.map { launcher -> launcher.metadata.installationPath })
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") { _ ->
            val compilerOptions = project.extensions.getByType(KotlinJvmExtension::class.java).compilerOptions

            it.jvmTarget.convention(compilerOptions.jvmTarget.map { jvmTarget -> jvmTarget.target })
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
