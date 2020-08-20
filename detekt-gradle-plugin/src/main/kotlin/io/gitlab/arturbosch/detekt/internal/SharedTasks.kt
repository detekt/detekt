package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal fun Project.registerDetektTask(
    name: String,
    extension: DetektExtension,
    configuration: Detekt.() -> Unit
): TaskProvider<Detekt> =
    tasks.register(name, Detekt::class.java) {
        it.debugProp.set(provider { extension.debug })
        it.parallelProp.set(provider { extension.parallel })
        it.disableDefaultRuleSetsProp.set(provider { extension.disableDefaultRuleSets })
        it.buildUponDefaultConfigProp.set(provider { extension.buildUponDefaultConfig })
        it.failFastProp.set(provider { extension.failFast })
        it.autoCorrectProp.set(provider { extension.autoCorrect })
        it.config.setFrom(provider { extension.config })
        it.ignoreFailuresProp.set(project.provider { extension.ignoreFailures })
        configuration(it)
    }

internal fun Project.registerCreateBaselineTask(
    name: String,
    extension: DetektExtension,
    configuration: DetektCreateBaselineTask.() -> Unit
): TaskProvider<DetektCreateBaselineTask> =
    tasks.register(name, DetektCreateBaselineTask::class.java) {
        it.config.setFrom(project.provider { extension.config })
        it.debug.set(project.provider { extension.debug })
        it.parallel.set(project.provider { extension.parallel })
        it.disableDefaultRuleSets.set(project.provider { extension.disableDefaultRuleSets })
        it.buildUponDefaultConfig.set(project.provider { extension.buildUponDefaultConfig })
        it.failFast.set(project.provider { extension.failFast })
        it.autoCorrect.set(project.provider { extension.autoCorrect })
        it.setIncludes(DetektPlugin.defaultIncludes)
        it.setExcludes(DetektPlugin.defaultExcludes)
        configuration(it)
    }

internal fun DetektReport.setDefaultIfUnset(default: File) {
    if (destination == null) {
        destination = default
    }
}
