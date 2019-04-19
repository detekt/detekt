package io.gitlab.arturbosch.detekt.extensions

enum class DetektReportType(val reportId: String, val extension: String) {

    XML("xml", "xml"),
    HTML("html", "html");

    companion object {
        fun isWellKnownReportId(reportId: String) = reportId in values().map(DetektReportType::reportId)
    }
}
