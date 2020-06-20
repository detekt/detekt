package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
open class DetektGenerateConfigTask : DefaultTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Classpath
    val detektClasspath = project.configurableFileCollection()

    private val invoker: DetektInvoker = DetektInvoker.create(project)
    private val configDir = project.mkdir("${project.rootDir}/$CONFIG_DIR_NAME")
    private val config = project.files("${configDir.canonicalPath}/$CONFIG_FILE")

    @TaskAction
    fun generateConfig() {
        val arguments = mutableListOf(
            GenerateConfigArgument,
            ConfigArgument(config)
        )

        try {
            if (config.singleFile.exists()) {
                logger.warn("Skipping config file generation; file already exists at ${config.singleFile}")
            } else {
                invoker.invokeCli(arguments.toList(), detektClasspath, name)
            }
        } catch (e: IllegalStateException) {
            logger.error("Unexpected error. Please raise an issue on detekt's issue tracker.", e)
        }
    }
}
