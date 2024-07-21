package io.gitlab.arturbosch.detekt.extensions

import io.gitlab.arturbosch.detekt.extensions.DetektReportType.HTML
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.MD
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.SARIF
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.XML
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import javax.inject.Inject

@Suppress("TooManyFunctions")
open class DetektReports @Inject constructor(@get:Internal val objects: ObjectFactory) {

    @get:Nested
    open val xml: DetektReport = objects.newInstance(DetektReport::class.java, XML)

    @get:Nested
    open val html: DetektReport = objects.newInstance(DetektReport::class.java, HTML)

    @get:Nested
    open val sarif: DetektReport = objects.newInstance(DetektReport::class.java, SARIF)

    @get:Nested
    open val md: DetektReport = objects.newInstance(DetektReport::class.java, MD)

    @get:Nested
    open val custom = mutableListOf<CustomDetektReport>()

    fun xml(action: Action<in DetektReport>): Unit = action.execute(xml)

    fun html(action: Action<in DetektReport>): Unit = action.execute(html)

    fun sarif(action: Action<in DetektReport>): Unit = action.execute(sarif)

    fun md(action: Action<in DetektReport>): Unit = action.execute(md)

    fun custom(action: Action<in CustomDetektReport>): Unit = action.execute(createAndAddCustomReport())

    private fun createAndAddCustomReport() =
        objects.newInstance(CustomDetektReport::class.java).apply { custom.add(this) }
}
