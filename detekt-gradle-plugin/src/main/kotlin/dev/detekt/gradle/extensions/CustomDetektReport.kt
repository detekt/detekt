package dev.detekt.gradle.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal

/**
 * Keep [outputLocation] internal: Gradle cannot associate a producing task with output properties of nested beans
 * that are part of a collection, so annotating it as an output property makes builds fail when the property is wired
 * into another task. The detekt task registers the custom report locations as its own outputs instead.
 */
abstract class CustomDetektReport {

    @get:Internal
    abstract var reportId: String?

    @get:Internal
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String = "CustomDetektReport(reportId=$reportId, outputLocation=$outputLocation)"
}
