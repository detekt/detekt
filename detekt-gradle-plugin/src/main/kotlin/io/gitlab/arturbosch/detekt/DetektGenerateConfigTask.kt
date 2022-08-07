package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

@CacheableTask
abstract class DetektGenerateConfigTask @Inject constructor(
    private val objects: ObjectFactory
) : DefaultTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Classpath
    abstract val detektClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val pluginClasspath: ConfigurableFileCollection

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val config: ConfigurableFileCollection

    private val defaultConfigPath = project.rootDir.toPath().resolve(CONFIG_DIR_NAME).resolve(CONFIG_FILE)

    private val configurationToUse: File
        get() = if (config.isEmpty) {
            objects.fileCollection().from(defaultConfigPath)
        } else {
            config
        }.last()

    @get:Internal
    internal val arguments: Provider<List<String>> = project.provider {
        listOf(
            GenerateConfigArgument,
            ConfigArgument(configurationToUse)
        ).flatMap(CliArgument::toArgument)
    }

    @TaskAction
    fun generateConfig() {
        if (configurationToUse.exists()) {
            logger.warn("Skipping config file generation; file already exists at $configurationToUse")
            return
        }

        Files.createDirectories(configurationToUse.parentFile.toPath())

        DetektInvoker.create(task = this).invokeCli(
            arguments = arguments.get(),
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name,
        )
    }
}
