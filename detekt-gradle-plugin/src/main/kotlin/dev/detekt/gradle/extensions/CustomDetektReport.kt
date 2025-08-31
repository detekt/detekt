package dev.detekt.gradle.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

abstract class CustomDetektReport {

    @get:Internal
    abstract var reportId: String?

    @get:OutputFile
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String = "CustomDetektReport(reportId=$reportId, outputLocation=$outputLocation)"
}
