package dev.detekt.gradle.extensions

import dev.detekt.gradle.extensions.DetektReportType.CHECKSTYLE
import dev.detekt.gradle.extensions.DetektReportType.HTML
import dev.detekt.gradle.extensions.DetektReportType.MARKDOWN
import dev.detekt.gradle.extensions.DetektReportType.SARIF
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import javax.inject.Inject

@Suppress("TooManyFunctions")
open class DetektReports @Inject constructor(@get:Internal val objects: ObjectFactory) {

    @get:Nested
    open val checkstyle: DetektReport = objects.newInstance(DetektReport::class.java, CHECKSTYLE)

    @get:Nested
    open val html: DetektReport = objects.newInstance(DetektReport::class.java, HTML)

    @get:Nested
    open val sarif: DetektReport = objects.newInstance(DetektReport::class.java, SARIF)

    @get:Nested
    open val markdown: DetektReport = objects.newInstance(DetektReport::class.java, MARKDOWN)

    @get:Nested
    open val custom = mutableListOf<CustomDetektReport>()

    fun checkstyle(action: Action<in DetektReport>): Unit = action.execute(checkstyle)

    fun html(action: Action<in DetektReport>): Unit = action.execute(html)

    fun sarif(action: Action<in DetektReport>): Unit = action.execute(sarif)

    fun markdown(action: Action<in DetektReport>): Unit = action.execute(markdown)

    fun custom(action: Action<in CustomDetektReport>): Unit = action.execute(createAndAddCustomReport())

    private fun createAndAddCustomReport() =
        objects.newInstance(CustomDetektReport::class.java).apply { custom.add(this) }
}
