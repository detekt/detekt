package dev.detekt.gradle

import dev.detekt.gradle.invoke.CliArgument
import dev.detekt.gradle.invoke.DetektInvoker
import dev.detekt.gradle.invoke.DetektWorkAction
import dev.detekt.gradle.invoke.GenerateConfigArgument
import dev.detekt.gradle.plugin.isWorkerApiEnabled
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
            GenerateConfigArgument(configFile.get())
        ).flatMap(CliArgument::toArgument)

    @TaskAction
    fun generateConfig() {
        if (configFile.get().asFile.exists()) {
            logger.warn("Skipping config file generation; file already exists at ${configFile.get().asFile}")
            return
        }

        if (providers.isWorkerApiEnabled()) {
            logger.info("Executing $name using Worker API")
            val workQueue = workerExecutor.processIsolation()

            workQueue.submit(DetektWorkAction::class.java) { workParameters ->
                workParameters.arguments.set(arguments)
                workParameters.classpath.setFrom(detektClasspath, pluginClasspath)
                workParameters.taskName.set(name)
            }
        } else {
            logger.info("Executing $name using DetektInvoker")
            DetektInvoker.create().invokeCli(
                arguments = arguments,
                classpath = detektClasspath.plus(pluginClasspath).files,
                taskName = name,
            )
        }
    }

    internal interface SingleExecutionBuildService : BuildService<BuildServiceParameters.None>
}
