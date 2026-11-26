package dev.detekt.gradle.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal

/**
 * [outputLocation] is intentionally not annotated as an output property: Gradle cannot associate a producing task
 * with output properties of nested beans that are part of a collection, so annotating it as an output property makes
 * builds fail when the property is wired into another task. Custom report locations are user-configured destinations,
 * not task outputs managed by Gradle; consuming them in another task therefore requires that task to declare its
 * ordering or dependency on the detekt tasks explicitly.
 */
abstract class CustomDetektReport {

    @get:Internal
    abstract var reportId: String?

    @get:Internal
    abstract val outputLocation: RegularFileProperty

    override fun toString(): String = "CustomDetektReport(reportId=$reportId, outputLocation=$outputLocation)"
}
