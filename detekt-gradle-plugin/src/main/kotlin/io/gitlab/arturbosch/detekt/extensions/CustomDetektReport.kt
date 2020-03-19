package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import java.io.File

class CustomDetektReport(private val project: Project) {

    @Internal
    val reportIdProp: Property<String> = project.objects.property(String::class.java)
    var reportId: String
        @Internal
        get() = reportIdProp.get()
        set(value) = reportIdProp.set(value)

    @OutputFile
    val destinationProperty: RegularFileProperty = project.objects.fileProperty()
    var destination: File
        @OutputFile
        get() = destinationProperty.get().asFile
        set(value) = destinationProperty.set(value)

    override fun toString(): String {
        return "CustomDetektReport(reportId=$reportId, destination=$destination)"
    }
}
