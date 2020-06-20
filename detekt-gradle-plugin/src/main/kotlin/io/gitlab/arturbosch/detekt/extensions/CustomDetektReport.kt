package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import java.io.File

class CustomDetektReport {

    @Internal
    var reportId: String? = null

    @OutputFile
    var destination: File? = null

    override fun toString(): String {
        return "CustomDetektReport(reportId=$reportId, destination=$destination)"
    }
}
