package dev.detekt.gradle.report

import io.github.detekt.sarif4k.SarifSerializer
import io.github.detekt.sarif4k.merge
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
        val existingFiles = input.filter { it.exists() }

        val xmls = existingFiles.filter { it.extension == "xml" }
        if (!xmls.isEmpty) {
            XmlReportMerger.merge(xmls.files, output.get().asFile)
            logger.lifecycle("Merged XML output to ${output.get().asFile.absolutePath}")
        }

        val sarifs = existingFiles.filter { it.extension == "sarif" || it.name.endsWith(".sarif.json") }
        if (!sarifs.isEmpty) {
            val sarif = sarifs
                .map { SarifSerializer.fromJson(it.readText()) }
                .reduce { acc, next -> acc.merge(next) }
            output.get().asFile.writeText(SarifSerializer.toJson(sarif))
            logger.lifecycle("Merged SARIF output to ${output.get().asFile.absolutePath}")
        }
    }
}
