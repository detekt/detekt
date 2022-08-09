package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_DIR_NAME
import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.CONFIG_FILE
import io.gitlab.arturbosch.detekt.invoke.ClasspathArgument
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import io.gitlab.arturbosch.detekt.invoke.GenerateCustomRuleConfigArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File
import java.nio.file.Files

@CacheableTask
abstract class DetektGenerateConfigTask : DefaultTask() {

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
            defaultConfigPath.toFile()
        } else {
            config.last()
        }

    @get:Input
    val generateOnlyFromCustomRules: Property<Boolean> = project.objects
        .property(Boolean::class.javaObjectType)
        .convention(false)

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: ConfigurableFileCollection

    private val defaultSourcePath = project.rootDir.toPath().resolve(SOURCE_DIR_NAME)

    private val sourceToUse: File
        get() = if (source.isEmpty) {
            defaultSourcePath.toFile()
        } else {
            source.last()
        }

    private val projectRoot = project.rootDir

    @get:Internal
    internal val arguments: Provider<List<String>> = project.provider {
        if (generateOnlyFromCustomRules.get()) {
            listOf(
                GenerateCustomRuleConfigArgument,
                InputArgument(sourceToUse),
                ClasspathArgument(projectRoot),
                ConfigArgument(configurationToUse)
            )
        } else {
            listOf(
                GenerateConfigArgument,
                ConfigArgument(configurationToUse)
            )
        }.flatMap(CliArgument::toArgument)
    }

    @TaskAction
    fun generateConfig() {
        if (configurationToUse.exists() && !generateOnlyFromCustomRules.get()) {
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

    @Suppress("UnnecessaryAbstractClass")
    abstract class SingleExecutionBuildService : BuildService<BuildServiceParameters.None>
}
