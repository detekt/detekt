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
import java.io.File

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
        fun isXmlReport(file: File): Boolean = file.extension == "xml"
        if (existingFiles.any(::isXmlReport)) {
            XmlReportMerger.merge(existingFiles.filter(::isXmlReport), output.get().asFile)
            logger.lifecycle("Merged XML output to ${output.get().asFile.absolutePath}")
        }

        fun isSarifReport(file: File): Boolean = file.extension == "sarif" ||
            file.name.endsWith(".sarif.json")
        if (existingFiles.any(::isSarifReport)) {
            SarifReportMerger.merge(existingFiles.filter(::isSarifReport), output.get().asFile)
            logger.lifecycle("Merged SARIF output to ${output.get().asFile.absolutePath}")
        }
    }
}
