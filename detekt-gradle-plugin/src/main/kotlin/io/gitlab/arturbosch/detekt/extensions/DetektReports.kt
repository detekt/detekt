package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.HTML
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.SARIF
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.TXT
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.XML
import org.gradle.api.model.ObjectFactory
import org.gradle.util.ConfigureUtil
import javax.inject.Inject

@Suppress("TooManyFunctions")
open class DetektReports @Inject constructor(val objects: ObjectFactory) {

    val xml = objects.newInstance(DetektReport::class.java, XML)

    val html = objects.newInstance(DetektReport::class.java, HTML)

    val txt = objects.newInstance(DetektReport::class.java, TXT)

    val sarif = objects.newInstance(DetektReport::class.java, SARIF)

    val custom = mutableListOf<CustomDetektReport>()

    fun xml(configure: DetektReport.() -> Unit): Unit = xml.configure()
    fun xml(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, xml)

    fun html(configure: DetektReport.() -> Unit): Unit = html.configure()
    fun html(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, html)

    fun txt(configure: DetektReport.() -> Unit): Unit = txt.configure()
    fun txt(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, txt)

    fun sarif(configure: DetektReport.() -> Unit): Unit = sarif.configure()
    fun sarif(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, sarif)

    fun custom(configure: CustomDetektReport.() -> Unit): Unit = createAndAddCustomReport().configure()
    fun custom(closure: Closure<*>): CustomDetektReport = ConfigureUtil.configure(closure, createAndAddCustomReport())

    private fun createAndAddCustomReport() =
        objects.newInstance(CustomDetektReport::class.java).apply { custom.add(this) }
}
