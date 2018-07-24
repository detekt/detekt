package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import org.gradle.api.Project

fun Project.detekt(configure: DetektExtension.() -> Unit): Unit =
		extensions.configure("detekt", configure)

fun Project.idea(configure: IdeaExtension.() -> Unit): Unit =
		extensions.configure("idea", configure)
