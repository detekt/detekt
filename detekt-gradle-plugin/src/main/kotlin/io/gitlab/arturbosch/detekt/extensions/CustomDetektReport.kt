package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import java.io.File

abstract class CustomDetektReport {

    @get:Internal
    abstract var reportId: String?

    @Deprecated("Use outputLocation.set(value)")
    @get:Internal
    var destination: File?
        get() = outputLocation.asFile.orNull
        set(value) {
            outputLocation.set(value)
        }

    @get:OutputFile
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String {
        return "CustomDetektReport(reportId=$reportId, outputLocation=$outputLocation)"
    }
}
