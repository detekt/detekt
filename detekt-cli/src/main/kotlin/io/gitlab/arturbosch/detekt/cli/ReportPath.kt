package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.out.HtmlOutputReport
import io.gitlab.arturbosch.detekt.cli.out.PlainOutputReport
import io.gitlab.arturbosch.detekt.cli.out.XmlOutputReport
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
data class ReportPath(val kind: String, val path: Path) {
	companion object {
		fun from(input: String): ReportPath {
			val parts = input.split(":", limit = 2)
			require(parts.size == 2) { "Must consist of exactly two parts (report-id:path)." }
			val (kind, path) = parts
			assertNotEmpty(kind, path)
			return ReportPath(defaultMapping(kind), Paths.get(path))
		}

		private fun assertNotEmpty(kind: String, path: String) {
			require(kind.isNotEmpty()) { "The kind of report must not be empty" }
			require(path.isNotEmpty()) { "The path of the report must not be empty" }
		}

		private fun defaultMapping(reportId: String) = when (reportId) {
			"plain" -> PlainOutputReport::class.java.simpleName
			"xml" -> XmlOutputReport::class.java.simpleName
			"html" -> HtmlOutputReport::class.java.simpleName
			else -> reportId
		}
	}
}
