package io.gitlab.arturbosch.detekt.report

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class ReportMergeTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val input: ConfigurableFileCollection

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun merge() {
        logger.info("Input")
        logger.info(input.files.joinToString(separator = "\n") { it.absolutePath })
        logger.info("Output = ${output.get().asFile.absolutePath}")
        val existingFiles = input.files.filter { it.exists() }

        val xmls = existingFiles.filter { it.extension == "xml" }
        if (xmls.isNotEmpty()) {
            XmlReportMerger.merge(xmls, output.get().asFile)
            logger.lifecycle("Merged XML output to ${output.get().asFile.absolutePath}")
        }

        val sarifs = existingFiles.filter { it.extension == "sarif" || it.name.endsWith(".sarif.json") }
        if (sarifs.isNotEmpty()) {
            SarifReportMerger.merge(sarifs, output.get().asFile)
            logger.lifecycle("Merged SARIF output to ${output.get().asFile.absolutePath}")
        }
    }
}
