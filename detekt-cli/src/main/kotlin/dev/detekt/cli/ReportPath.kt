package dev.detekt.cli

import java.nio.file.Path
import kotlin.io.path.Path

data class ReportPath(val kind: String, val path: Path) {

    companion object {
        fun from(input: String): ReportPath {
            val parts = input.split(":", limit = 2)

            require(parts.size == 2) { "Input '$input' must consist of two parts (report-id:path)." }

            val kind = parts[0]
            val path = parts[1]
            require(kind.isNotEmpty()) { "The kind of report must not be empty (path - $path)" }
            require(path.isNotEmpty()) { "The path of the report must not be empty (kind - $kind)" }
            return ReportPath(kind, Path(path))
        }
    }
}
