package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import java.io.File
import javax.inject.Inject

open class CustomDetektReport @Inject constructor(objects: ObjectFactory) {

    @Internal
    var reportId: String? = null

    @Deprecated("Use outputLocation.set(value)")
    @get:Internal
    var destination: File?
        get() = outputLocation.asFile.getOrNull()
        set(value) {
            outputLocation.set(value)
        }

    @OutputFile
    val outputLocation: RegularFileProperty = objects.fileProperty()

    override fun toString(): String {
        return "CustomDetektReport(reportId=$reportId, outputLocation=$outputLocation)"
    }
}
