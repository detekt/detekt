package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DetektWorkAction
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.workers.WorkerExecutor
import java.nio.file.Files
import javax.inject.Inject

@CacheableTask
abstract class DetektGenerateConfigTask @Inject constructor(
    private val workerExecutor: WorkerExecutor,
    private val providers: ProviderFactory,
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
    abstract val configFile: RegularFileProperty

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

        if (providers.gradleProperty(USE_WORKER_API).getOrElse("false") == "true") {
            logger.info("Executing $name using Worker API")
            val workQueue = workerExecutor.processIsolation { workerSpec ->
                workerSpec.classpath.from(detektClasspath)
                workerSpec.classpath.from(pluginClasspath)
            }

            workQueue.submit(DetektWorkAction::class.java) { workParameters ->
                workParameters.arguments.set(arguments)
                workParameters.taskName.set(name)
            }
        } else {
            logger.info("Executing $name using DetektInvoker")
            DetektInvoker.create().invokeCli(
                arguments = arguments,
                classpath = detektClasspath.plus(pluginClasspath),
                taskName = name,
            )
        }
    }

    interface SingleExecutionBuildService : BuildService<BuildServiceParameters.None>
}
