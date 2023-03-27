package io.gitlab.arturbosch.detekt

import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath

interface DetektTask : Task {
    @get:Classpath
    val detektClasspath: ConfigurableFileCollection

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection
}
