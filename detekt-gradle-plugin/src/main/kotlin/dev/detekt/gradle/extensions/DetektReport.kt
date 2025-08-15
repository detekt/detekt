package dev.detekt.gradle.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import javax.inject.Inject

abstract class DetektReport @Inject constructor(@get:Internal val type: DetektReportType) {
    @get:Input
    abstract val required: Property<Boolean>

    @get:OutputFile
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String = "DetektReport(type='$type', required=$required, outputLocation=$outputLocation)"
}
