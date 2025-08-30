package dev.detekt.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath

interface DetektCliTool : Task {
    @get:Classpath
    val detektClasspath: ConfigurableFileCollection

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection
}
