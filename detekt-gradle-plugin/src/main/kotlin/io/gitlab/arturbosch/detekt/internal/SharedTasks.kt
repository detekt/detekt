package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.toolchain.JavaToolchainService

internal fun Project.registerDetektTask(
    name: String,
    extension: DetektExtension,
    configuration: Detekt.() -> Unit
): TaskProvider<Detekt> =
    tasks.register(name, Detekt::class.java) {
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
        it.config.setFrom(provider { extension.config })
        it.basePath.convention(extension.basePath.map { basePath -> basePath.asFile.absolutePath })
        it.allRules.convention(extension.allRules)
        configuration(it)
    }

internal fun Project.registerCreateBaselineTask(
    name: String,
    extension: DetektExtension,
    configuration: DetektCreateBaselineTask.() -> Unit
): TaskProvider<DetektCreateBaselineTask> =
    tasks.register(name, DetektCreateBaselineTask::class.java) {
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

        it.config.setFrom(project.provider { extension.config })
        it.debug.convention(extension.debug)
        it.parallel.convention(extension.parallel)
        it.disableDefaultRuleSets.convention(extension.disableDefaultRuleSets)
        it.buildUponDefaultConfig.convention(extension.buildUponDefaultConfig)
        it.autoCorrect.convention(extension.autoCorrect)
        it.ignoreFailures.convention(extension.ignoreFailures)
        it.basePath.convention(extension.basePath.map { basePath -> basePath.asFile.absolutePath })
        it.allRules.convention(extension.allRules)
        configuration(it)
    }
