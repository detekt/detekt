package dev.detekt.gradle.extensions

enum class DetektReportType(val reportId: String, val extension: String) {

    CHECKSTYLE("checkstyle", "xml"),
    HTML("html", "html"),
    SARIF("sarif", "sarif"),
    MARKDOWN("markdown", "md"),
    ;

    internal companion object {
        fun isWellKnownReportId(reportId: String) = reportId in values().map(DetektReportType::reportId)
    }
}
