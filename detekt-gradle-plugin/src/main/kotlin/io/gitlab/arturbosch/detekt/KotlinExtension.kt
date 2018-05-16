package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

fun Project.detekt(configure: DetektExtension.() -> Unit): Unit =
		extensions.configure("detekt", configure)
