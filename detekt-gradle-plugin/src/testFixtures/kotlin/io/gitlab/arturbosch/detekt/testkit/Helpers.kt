package io.gitlab.arturbosch.detekt.testkit

import org.gradle.api.Task

fun Task.dependenciesAsNames() = this.taskDependencies.getDependencies(this).map { it.name }
fun Task.dependenciesAsPaths() = this.taskDependencies.getDependencies(this).map { it.path }
