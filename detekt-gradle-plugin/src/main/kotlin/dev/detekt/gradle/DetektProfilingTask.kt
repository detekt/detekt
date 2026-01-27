package dev.detekt.gradle

import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.DEFAULT_TOP_RULES_TO_SHOW
import dev.detekt.gradle.util.RuleProfilingReport
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

/**
 * Task that processes profiling output from a Detekt task and displays timing analysis.
 *
 * This task depends on a [Detekt] task configured with profiling enabled,
 * and processes the profiling CSV output to display per-rule timing statistics.
 */
@DisableCachingByDefault
abstract class DetektProfilingTask @Inject constructor() : DefaultTask() {

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Process detekt profiling output and display timing analysis"

        outputs.upToDateWhen { false }
        outputs.cacheIf { false }
    }

    /**
     * The profiling CSV output file(s) produced by the Detekt task.
     * This file is located in the reports directory by default.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val profilingInput: RegularFileProperty

    /**
     * Location to copy the profiling output.
     * If not set, the value of [profilingInput] will be used.
     */
    @get:OutputFile
    abstract val profilingOutput: RegularFileProperty

    /**
     * Whether to print the profiling report to the console.
     * Defaults to false to avoid cluttering output in multi-project builds.
     * Set to true to display formatted profiling results.
     */
    @get:Console
    abstract val printResults: Property<Boolean>

    /**
     * Number of slowest rules to display in console output.
     * Defaults to [DEFAULT_TOP_RULES_TO_SHOW]. Can be configured via the detekt extension or per-task.
     */
    @get:Console
    abstract val topRulesToShow: Property<Int>

    @TaskAction
    fun processProfilingResults() {
        val inputFile = profilingInput.get().asFile
        val outputFile = profilingOutput.get().asFile

        if (inputFile == null || !inputFile.exists()) {
            logger.warn("Profiling input file not found. The detekt task may have failed or not run.")
            return
        }

        // Copy to output location if it isn't the same as the input location
        if (inputFile.absolutePath != outputFile.absolutePath) {
            outputFile.parentFile?.mkdirs()
            inputFile.copyTo(outputFile, overwrite = true)
        }

        displayProfilingResults(inputFile)
    }

    private fun displayProfilingResults(outputFile: File) {
        if (!outputFile.exists() || outputFile.length() == 0L) {
            logger.lifecycle("Profiling output (CSV): ${outputFile.toPath().toUri()}")
            return
        }

        // Print formatted profiling report if requested
        if (printResults.get()) {
            val metrics = RuleProfilingReport.parseAndAggregate(outputFile)

            if (metrics.isNotEmpty()) {
                val report = RuleProfilingReport.render(metrics, topRulesToShow.get())
                if (report != null) {
                    logger.lifecycle(report)
                    logger.lifecycle("")
                }
            }
        }

        // Always print the output file URI
        logger.lifecycle("Profiling output (CSV): ${outputFile.toPath().toUri()}")
    }

    companion object {
        fun register(
            project: Project,
            taskName: String,
            consumableConfigurationName: String = "${taskName}Publisher",
            extension: DetektExtension,
            detektTaskProvider: TaskProvider<Detekt>,
        ): TaskProvider<DetektProfilingTask> {
            // Configure the Detekt task to enable profiling and write to reports directory
            var profile = false
            val provider = project.provider { profile }
            detektTaskProvider.configure { detekt ->
                detekt.profile.set(provider)
                detekt.profileOutput.convention(
                    extension.reportsDir.file("$taskName-profiling.csv")
                )
            }

            return project.tasks.register(taskName, DetektProfilingTask::class.java) { profilingTask ->
                profile = true
                profilingTask.dependsOn(detektTaskProvider)

                // Map outputs from Detekt to inputs of profiling task
                // Use from() instead of set() to allow the file to not exist at configuration time
                profilingTask.profilingInput.set(detektTaskProvider.flatMap { it.profileOutput })

                // Don't print results for individual tasks by default - aggregate task will show the report
                profilingTask.printResults.convention(false)

                profilingTask.topRulesToShow.convention(extension.topRulesToShow)

                profilingTask.profilingOutput.convention(detektTaskProvider.flatMap { it.profileOutput })
            }.also { profilingProvider ->
                if (project != project.rootProject) {
                    configureProfilingPublisher(
                        project = project,
                        taskName = taskName,
                        configurationName = consumableConfigurationName,
                        profilingTaskProvider = profilingProvider
                    )
                }
            }
        }

        private fun configureProfilingPublisher(
            project: Project,
            taskName: String,
            configurationName: String,
            profilingTaskProvider: TaskProvider<DetektProfilingTask>,
        ) {
            project.configurations.create(configurationName) { config ->
                config.isCanBeConsumed = true
                config.isCanBeResolved = false
                config.description = "Publishes profiling CSV artifacts from $taskName in this project"

                // Wire the profiling task output as an artifact
                config.outgoing.artifact(profilingTaskProvider.flatMap { it.profilingOutput })
            }
        }
    }
}
