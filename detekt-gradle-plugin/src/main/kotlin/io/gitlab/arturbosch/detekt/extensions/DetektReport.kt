package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import java.io.File
import javax.inject.Inject

abstract class DetektReport @Inject constructor(val type: DetektReportType) {

    @Deprecated("Use required.set(value)")
    var enabled: Boolean?
        get() = required.get()
        set(value) = required.set(value)

    @Deprecated("Use outputLocation.set(value)")
    var destination: File?
        get() = outputLocation.asFile.orNull
        set(value) {
            outputLocation.set(value)
        }

    @get:Input
    abstract val required: Property<Boolean>

    @get:OutputFile
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String {
        return "DetektReport(type='$type', required=$required, outputLocation=$outputLocation)"
    }

    companion object {
        const val DEFAULT_FILENAME = "detekt"
    }
}
