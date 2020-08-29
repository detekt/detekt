package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.nio.file.Files
import javax.inject.Inject

@CacheableTask
open class DetektGenerateConfigTask @Inject constructor(
    private val objects: ObjectFactory
) : DefaultTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Classpath
    val detektClasspath: ConfigurableFileCollection = project.objects.fileCollection()

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val config: ConfigurableFileCollection = project.objects.fileCollection()

    private val invoker: DetektInvoker = DetektInvoker.create(project)
    private val defaultConfigPath = project.rootDir.toPath().resolve(CONFIG_DIR_NAME).resolve(CONFIG_FILE)

    @TaskAction
    fun generateConfig() {
        val configurationToUse = if (config.isEmpty) {
            objects.fileCollection().from(defaultConfigPath)
        } else {
            config
        }

        if (configurationToUse.last().exists()) {
            logger.warn("Skipping config file generation; file already exists at ${configurationToUse.last()}")
            return
        }

        Files.createDirectories(configurationToUse.last().parentFile.toPath())

        val arguments = mutableListOf(
            GenerateConfigArgument,
            ConfigArgument(configurationToUse.last())
        )

        invoker.invokeCli(arguments.toList(), detektClasspath, name)
    }
}
