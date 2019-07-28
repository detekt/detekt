package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.TxtOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import java.nio.file.Path
import java.nio.file.Paths

data class ReportPath(val kind: String, val path: Path) {

    companion object {
        private const val NUM_OF_PARTS_UNIX = 2
        private const val NUM_OF_PARTS_WINDOWS = 3
        private const val REPORT_PATH_SEPARATOR = ":"
        private const val ILLEGAL_PARTS_SIZE_ERROR =
                "Must consist of two parts for Unix OSs or three for Windows (report-id:path)."

        fun from(input: String): ReportPath {
            val parts = input.split(REPORT_PATH_SEPARATOR)
            val partsSize = parts.size

            require(partsSize == NUM_OF_PARTS_UNIX || partsSize == NUM_OF_PARTS_WINDOWS) { ILLEGAL_PARTS_SIZE_ERROR }

            val kind = parts[0]
            val path = when (partsSize) {
                NUM_OF_PARTS_UNIX -> parts[1]
                NUM_OF_PARTS_WINDOWS -> parts.slice(1 until partsSize).joinToString(REPORT_PATH_SEPARATOR)
                else -> throw IllegalStateException(ILLEGAL_PARTS_SIZE_ERROR)
            }

            assertNotEmpty(kind, path)
            return ReportPath(defaultMapping(kind), Paths.get(path))
        }

        private fun assertNotEmpty(kind: String, path: String) {
            require(kind.isNotEmpty()) { "The kind of report must not be empty" }
            require(path.isNotEmpty()) { "The path of the report must not be empty" }
        }

        private fun defaultMapping(reportId: String) = when (reportId) {
            "txt" -> TxtOutputReport::class.java.simpleName
            "xml" -> XmlOutputReport::class.java.simpleName
            "html" -> HtmlOutputReport::class.java.simpleName
            else -> reportId
        }
    }
}
