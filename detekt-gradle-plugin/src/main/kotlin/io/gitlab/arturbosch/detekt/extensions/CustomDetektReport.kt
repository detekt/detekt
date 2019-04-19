package io.gitlab.arturbosch.detekt.extensions

import io.gitlab.arturbosch.detekt.internal.fileProperty
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import java.io.File

class CustomDetektReport(private val project: Project) {

    @Internal
    val typeProperty: Property<String> = project.objects.property(String::class.java)
    var type: String
        get() = typeProperty.get()
        set(value) = typeProperty.set(value)

    @OutputFile
    val destinationProperty: RegularFileProperty = project.fileProperty()
    var destination: File
        get() = destinationProperty.get().asFile
        set(value) = destinationProperty.set(value)

    override fun toString(): String {
        return "CustomDetektReport(type=$type, destination=$destination)"
    }
}
