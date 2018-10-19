package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.HTML
import io.gitlab.arturbosch.detekt.extensions.DetektReportType.XML
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class DetektReports(project: Project) {

	val xml = DetektReport(XML, project)

	val html = DetektReport(HTML, project)

	fun xml(configure: DetektReport.() -> Unit) = xml.configure()
	fun xml(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, xml)

	fun html(configure: DetektReport.() -> Unit) = html.configure()
	fun html(closure: Closure<*>): DetektReport = ConfigureUtil.configure(closure, html)
}
