package dev.detekt.gradle.extensions

import org.gradle.api.artifacts.dsl.Dependencies
import org.gradle.api.artifacts.dsl.DependencyCollector

interface DetektDependencies : Dependencies {
    val plugin: DependencyCollector
}
