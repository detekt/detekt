package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * Generates an XML report following the structure of a Checkstyle report.
 */
class XmlOutputFormat : OutputFormat() {

	private sealed class MessageType(val label: String) {
		class Warning : MessageType("warning")
		class Info : MessageType("info")
		class Fatal : MessageType("fatal")
		class Error : MessageType("error")
	}

	override fun render(smells: List<Finding>): String {
		val lines = ArrayList<String>()
		lines += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		lines += "<checkstyle version=\"4.3\">"

		smells.groupBy { it.location.file }.forEach { fileName, findings ->
			lines += "<file name=\"${fileName.toXmlString()}\">"
			findings.forEach {
				lines += arrayOf(
						"\t<error line=\"${it.location.source.line.toXmlString()}\"",
						"column=\"${it.location.source.column.toXmlString()}\"",
						"severity=\"${it.messageType.label.toXmlString()}\"",
						"message=\"${(it.issue.description).toXmlString()}\"",
						"source=\"${"detekt.${it.id.toXmlString()}"}\" />"
				).joinToString(separator = " ")
			}
			lines += "</file>"
		}

		lines += "</checkstyle>"
		return lines.joinToString(separator = "\n")
	}

	private val Finding.messageType: MessageType
		get() = when (issue.severity) {
			Severity.CodeSmell -> MessageType.Warning()
			Severity.Style -> MessageType.Warning()
			Severity.Warning -> MessageType.Warning()
			Severity.Maintainability -> MessageType.Warning()
			Severity.Defect -> MessageType.Error()
			Severity.Minor -> MessageType.Info()
			Severity.Security -> MessageType.Fatal()
			Severity.Performance -> MessageType.Warning()
		}

	private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}
