package io.gitlab.arturbosch.detekt.extensions

import java.io.File

data class DetektReport(
    val type: DetektReportType,
    var enabled: Boolean? = null,
    var destination: File? = null
) {
    companion object {
        const val DEFAULT_FILENAME = "detekt"
    }
}
