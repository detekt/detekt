package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.HTML
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.TXT
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.XML
import org.gradle.util.ConfigureUtil

class DetektReports {

    val xml = DetektReport(XML)

    val html = DetektReport(HTML)

    val txt = DetektReport(TXT)

    val custom = mutableListOf<CustomDetektReport>()

    fun xml(configure: DetektReport.() -> Unit) = xml.configure()
    fun xml(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, xml)

    fun html(configure: DetektReport.() -> Unit) = html.configure()
    fun html(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, html)

    fun txt(configure: DetektReport.() -> Unit) = txt.configure()
    fun txt(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, txt)

    fun custom(configure: CustomDetektReport.() -> Unit) = createAndAddCustomReport().configure()
    fun custom(closure: Closure<*>): CustomDetektReport = ConfigureUtil.configure(closure, createAndAddCustomReport())

    private fun createAndAddCustomReport() = CustomDetektReport().apply { custom.add(this) }
}
