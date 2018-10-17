package io.gitlab.arturbosch.detekt.extensions

enum class DetektReportType(val reportName: String, val extension: String = reportName) {

	XML("xml"),
	HTML("html")
}
