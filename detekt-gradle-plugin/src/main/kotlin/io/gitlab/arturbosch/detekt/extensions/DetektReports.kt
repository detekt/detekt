package io.gitlab.arturbosch.detekt.extensions

import io.gitlab.arturbosch.detekt.extensions.DetektReportType.HTML
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.SARIF
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.TXT
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.XML
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import javax.inject.Inject

@Suppress("TooManyFunctions")
open class DetektReports @Inject constructor(private val objects: ObjectFactory) {

    @Nested
    val xml: DetektReport = objects.newInstance(DetektReport::class.java, XML)

    @Nested
    val html: DetektReport = objects.newInstance(DetektReport::class.java, HTML)

    @Nested
    val txt: DetektReport = objects.newInstance(DetektReport::class.java, TXT)

    @Nested
    val sarif: DetektReport = objects.newInstance(DetektReport::class.java, SARIF)

    @Nested
    val custom = mutableListOf<CustomDetektReport>()

    fun xml(action: Action<in DetektReport>): Unit = action.execute(xml)

    fun html(action: Action<in DetektReport>): Unit = action.execute(html)

    fun txt(action: Action<in DetektReport>): Unit = action.execute(txt)

    fun sarif(action: Action<in DetektReport>): Unit = action.execute(sarif)

    fun custom(action: Action<in CustomDetektReport>): Unit = action.execute(createAndAddCustomReport())

    private fun createAndAddCustomReport() =
        objects.newInstance(CustomDetektReport::class.java).apply { custom.add(this) }
}
