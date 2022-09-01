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
        @Suppress("DEPRECATION")
        with(extension.reports) {
            if (xml.outputLocation.isPresent) {
                logger.warn(
                    "XML report location set on detekt {} extension will be ignored for $name task. See " +
                        "https://detekt.dev/gradle.html#reports"
                )
            }
            if (sarif.outputLocation.isPresent) {
                logger.warn(
                    "SARIF report location set on detekt {} extension will be ignored for $name task. See " +
                        "https://detekt.dev/gradle.html#reports"
                )
            }
            if (txt.outputLocation.isPresent) {
                logger.warn(
                    "TXT report location set on detekt {} extension will be ignored for $name task. See " +
                        "https://detekt.dev/gradle.html#reports"
                )
            }
            if (html.outputLocation.isPresent) {
                logger.warn(
                    "HTML report location set on detekt {} extension will be ignored for $name task. See " +
                        "https://detekt.dev/gradle.html#reports"
                )
            }
        }

        project.plugins.withType(JavaBasePlugin::class.java) { _ ->
            val toolchain = project.extensions.getByType(JavaPluginExtension::class.java).toolchain

            // acquire a provider that returns the launcher for the toolchain
            val service = project.extensions.getByType(JavaToolchainService::class.java)
            val defaultLauncher = service.launcherFor(toolchain)
            it.jdkHome.convention(defaultLauncher.map { launcher -> launcher.metadata.installationPath })
            it.jvmTargetProp.convention(
                defaultLauncher.map { launcher ->
                    JavaVersion.toVersion(launcher.metadata.languageVersion.asInt()).toString()
                }
            )
        }

        it.debugProp.convention(provider { extension.debug })
        it.parallelProp.convention(provider { extension.parallel })
        it.disableDefaultRuleSetsProp.convention(provider { extension.disableDefaultRuleSets })
        it.buildUponDefaultConfigProp.convention(provider { extension.buildUponDefaultConfig })
        it.failFastProp.convention(provider { @Suppress("DEPRECATION") extension.failFast })
        it.autoCorrectProp.convention(provider { extension.autoCorrect })
        it.config.setFrom(provider { extension.config })
        it.ignoreFailuresProp.convention(project.provider { extension.ignoreFailures })
        it.basePathProp.convention(extension.basePath)
        it.allRulesProp.convention(provider { extension.allRules })
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
            it.jvmTargetProp.convention(
                defaultLauncher.map { launcher ->
                    JavaVersion.toVersion(launcher.metadata.languageVersion.asInt()).toString()
                }
            )
        }

        it.config.setFrom(project.provider { extension.config })
        it.debug.convention(project.provider { extension.debug })
        it.parallel.convention(project.provider { extension.parallel })
        it.disableDefaultRuleSets.convention(project.provider { extension.disableDefaultRuleSets })
        it.buildUponDefaultConfig.convention(project.provider { extension.buildUponDefaultConfig })
        @Suppress("DEPRECATION") it.failFast.convention(project.provider { extension.failFast })
        it.autoCorrect.convention(project.provider { extension.autoCorrect })
        it.basePathProp.convention(extension.basePath)
        it.allRules.convention(provider { extension.allRules })
        configuration(it)
    }
