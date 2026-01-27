package dev.detekt.gradle

import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.DEFAULT_TOP_RULES_TO_SHOW
import dev.detekt.gradle.util.RuleProfilingReport
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault

/**
 * Task that aggregates profiling CSV files from multiple sources into a combined report.
 */
@DisableCachingByDefault
abstract class DetektProfilingAggregateTask : DefaultTask() {

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Aggregates detekt profiling results from all subprojects"

        outputs.upToDateWhen { false }
        outputs.cacheIf { false }
    }

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val profilingFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val aggregatedOutput: RegularFileProperty

    /**
     * Number of slowest rules to display in console output and summary report.
     * Defaults to [DEFAULT_TOP_RULES_TO_SHOW]. Can be configured via the detekt extension or per-task.
     */
    @get:Console
    abstract val topRulesToShow: Property<Int>

    @TaskAction
    fun aggregate() {
        val inputFiles = profilingFiles.files.filter { it.exists() && it.length() > 0 }

        if (inputFiles.isEmpty()) {
            logger.lifecycle("No profiling files to aggregate")
            aggregatedOutput.get().asFile.writeText("No profiling data available\n")
            return
        }

        // Parse and aggregate metrics
        val aggregated = RuleProfilingReport.parseAndAggregate(inputFiles.toList())

        if (aggregated.isEmpty()) {
            logger.lifecycle("No profiling data to aggregate")
            aggregatedOutput.get().asFile.writeText("No profiling data available\n")
            return
        }

        // Write CSV to output file
        val outputFile = aggregatedOutput.get().asFile
        RuleProfilingReport.writeCsv(aggregated, outputFile)

        // Print formatted results to console
        val report = RuleProfilingReport.render(aggregated, topRulesToShow.get(), sourceCount = inputFiles.size)
        if (report != null) {
            logger.lifecycle(report)
        }

        logger.lifecycle("")
        logger.lifecycle("Full profiling report (CSV): ${outputFile.toURI()}")
    }

    companion object {
        fun register(
            project: Project,
            taskName: String,
            extension: DetektExtension,
            rootProfilingTaskProvider: TaskProvider<DetektProfilingTask>?,
        ) {
            val profilingConsumer = project.configurations.create("detektProfilingConsumer") { config ->
                config.isCanBeConsumed = false
                config.isCanBeResolved = true
                config.description = "Consumes Detekt profiling CSV artifacts from subprojects"
            }

            // Add dependencies on all subprojects' publisher configurations
            val publisherConfigName = "${taskName}Publisher"
            project.subprojects.forEach { subproject ->
                profilingConsumer.dependencies.add(
                    project.dependencies.project(
                        mapOf("path" to subproject.isolated.path, "configuration" to publisherConfigName)
                    )
                )
            }

            // Get aggregated files with lenient view (allows missing artifacts from projects without profiling)
            val aggregatedFiles = profilingConsumer.incoming.artifactView { view ->
                view.lenient(true)
            }.files

            val reportsDir = project.extensions.getByType(ReportingExtension::class.java).baseDirectory.dir("detekt")

            project.tasks.register(taskName, DetektProfilingAggregateTask::class.java) { task ->
                task.profilingFiles.from(aggregatedFiles)
                // Also include this project's own profiling output if present
                if (rootProfilingTaskProvider != null) {
                    task.profilingFiles.from(rootProfilingTaskProvider.flatMap { it.profilingOutput })
                    task.dependsOn(rootProfilingTaskProvider)
                }
                task.aggregatedOutput.convention(reportsDir.map { it.file("$taskName-profiling-aggregate.csv") })
                task.topRulesToShow.convention(extension.topRulesToShow)
            }
        }
    }
}
