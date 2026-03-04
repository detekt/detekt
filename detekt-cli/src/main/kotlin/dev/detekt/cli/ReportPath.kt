package dev.detekt.cli

import java.nio.file.Path
import kotlin.io.path.Path

data class ReportPath(val kind: String, val path: Path) {

    companion object {
        private const val NUM_OF_PARTS_UNIX = 2
        private const val NUM_OF_PARTS_WINDOWS = 3
        private const val REPORT_PATH_SEPARATOR = ":"

        fun from(input: String): ReportPath {
            val parts = input.split(REPORT_PATH_SEPARATOR)

            val path = when (val partsSize = parts.size) {
                NUM_OF_PARTS_UNIX -> parts[1]

                NUM_OF_PARTS_WINDOWS -> parts.slice(1..<partsSize).joinToString(REPORT_PATH_SEPARATOR)

                else -> error(
                    "Input '$input' must consist of two parts for Unix OSs or three for Windows (report-id:path)."
                )
            }

            val kind = parts[0]
            require(kind.isNotEmpty()) { "The kind of report must not be empty (path - $path)" }
            require(path.isNotEmpty()) { "The path of the report must not be empty (kind - $kind)" }
            return ReportPath(kind, Path(path))
        }
    }
}
