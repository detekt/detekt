package io.gitlab.arturbosch.detekt.extensions

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

class DetektReports {

	val xml = DetektReport("xml")

	val html = DetektReport("html")

	val all = listOf(xml, html)

	fun forEach(configure: (DetektReport) -> Unit) = all.forEach(configure)

	fun withName(name: String, configure: DetektReport.() -> Unit) = all.find { it.name == name }?.let(configure)

	fun xml(configure: DetektReport.() -> Unit) = xml.configure()
	fun xml(closure: Closure<*>) = ConfigureUtil.configure(closure, xml)

	fun html(configure: DetektReport.() -> Unit) = html.configure()
	fun html(closure: Closure<*>) = ConfigureUtil.configure(closure, html)
}
