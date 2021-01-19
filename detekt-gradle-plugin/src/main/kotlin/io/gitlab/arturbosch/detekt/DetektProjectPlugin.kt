package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.report.XmlOutputMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension

class DetektProjectPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        require(project.isRootProject()) {
            "DetektProjectPlugin should be applied to rootProject only."
        }
        project.pluginManager.apply(ReportingBasePlugin::class.java)
        project.registerMergeReportTask()
    }

    private fun Project.registerMergeReportTask() {
        val xmlOutputMergeTask = tasks.register(MERGE_XML_REPORT, XmlOutputMergeTask::class.java) { task ->
            task.description = "Merge XML report for all individual detekt tasks in this project"
            task.output.set(
                project.extensions.getByType(ReportingExtension::class.java).file(MERGE_XML_FILE_RELATIVE_PATH)
            )
        }
        // Delay the configuration so that detektTask.reports.xml.destination is always set
        // when configuring the input of merge task.
        project.afterEvaluate { project ->
            project.subprojects { subproject ->
                subproject.tasks.withType(Detekt::class.java).configureEach { detektTask ->
                    xmlOutputMergeTask.configure { it.input.from(detektTask.reports.xml.destination) }
                }
            }
        }
    }

    private fun Project.isRootProject() = path == ":"

    companion object {
        private const val MERGE_XML_REPORT = "mergeXmlReport"
        private const val MERGE_XML_FILE_RELATIVE_PATH = "detekt/merged.xml"
    }
}
