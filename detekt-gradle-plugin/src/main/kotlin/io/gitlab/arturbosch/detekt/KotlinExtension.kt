@file:Suppress("DeprecatedCallableAddReplaceWith")

package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

@Deprecated(
    """Either apply detekt plugin to root project or follow this advice:
https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:multi_project_builds_applying_plugins"""
)
fun Project.detekt(configure: DetektExtension.() -> Unit) =
    extensions.configure(DetektExtension::class.java, configure)
