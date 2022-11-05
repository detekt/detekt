package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

@CacheableTask
abstract class DetektGenerateConfigTask @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Classpath
    abstract val detektClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val pluginClasspath: ConfigurableFileCollection

    @get:OutputFile
    val configFile: RegularFileProperty = objects.fileProperty().convention { configurationToUse }

    @get:Internal
    @get:Deprecated("Replaced with configFile property")
    abstract val config: ConfigurableFileCollection

    private val defaultConfigPath = project.rootDir.toPath().resolve(CONFIG_DIR_NAME).resolve(CONFIG_FILE)

    @Suppress("DEPRECATION")
    private val configurationToUse: File
        get() = if (config.isEmpty) {
            defaultConfigPath.toFile()
        } else {
            config.last()
        }

    @get:Internal
    internal val arguments
        get() = listOf(
            GenerateConfigArgument,
            ConfigArgument(configFile.get())
        ).flatMap(CliArgument::toArgument)

    @TaskAction
    fun generateConfig() {
        if (configFile.get().asFile.exists()) {
            logger.warn("Skipping config file generation; file already exists at ${configFile.get().asFile}")
            return
        }

        Files.createDirectories(configFile.get().asFile.parentFile.toPath())

        DetektInvoker.create(task = this).invokeCli(
            arguments = arguments,
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name,
        )
    }

    @Suppress("UnnecessaryAbstractClass")
    abstract class SingleExecutionBuildService : BuildService<BuildServiceParameters.None>
}
