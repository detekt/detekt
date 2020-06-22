package io.gitlab.arturbosch.detekt.extensions

import java.io.File

class DetektReport(val type: DetektReportType) {

    var enabled: Boolean? = null

    var destination: File? = null

    override fun toString(): String {
        return "DetektReport(type='$type', enabled=$enabled, destination=$destination)"
    }

    companion object {
        const val DEFAULT_FILENAME = "detekt"
    }
}
