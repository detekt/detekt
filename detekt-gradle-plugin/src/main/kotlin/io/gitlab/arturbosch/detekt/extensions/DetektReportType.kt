package io.gitlab.arturbosch.detekt.extensions

enum class DetektReportType(val typeId: String, val extension: String) {

    XML("xml", "xml"),
    HTML("html", "html");

    companion object {
        fun isWellKnownReportTypeId(typeId: String) = typeId in values().map(DetektReportType::typeId)
    }
}
