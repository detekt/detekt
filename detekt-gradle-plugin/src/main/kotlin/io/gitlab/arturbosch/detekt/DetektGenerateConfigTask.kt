package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

open class DetektGenerateConfigTask : SourceTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        @Internal
        get() = source
        set(value) = setSource(value)

    @Classpath
    val detektClasspath = project.configurableFileCollection()

    @TaskAction
    fun generateConfig() {

        val configDir = project.mkdir("${project.rootDir}/$CONFIG_DIR_NAME")
        val config = project.files("${configDir.canonicalPath}/$CONFIG_FILE")

        val arguments = mutableListOf(
            GenerateConfigArgument,
            ConfigArgument(config),
            InputArgument(source)
        )

        if (config.first().exists()) {
            project.logger.warn("Skipping config file generation. Config file already exists at ${config.first()}")
        } else {
            DetektInvoker.create(project).invokeCli(arguments.toList(), detektClasspath, name)
        }
    }
}
