package io.gitlab.arturbosch.detekt.internal

import dev.detekt.gradle.plugin.internal.conventionCompat
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaToolchainService

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
