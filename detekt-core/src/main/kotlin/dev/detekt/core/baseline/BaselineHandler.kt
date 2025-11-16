package dev.detekt.core.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

internal class BaselineHandler : DefaultHandler() {

    private var current: String? = null
    private var content: String = ""
    private val currentIssues = mutableSetOf<String>()
    private val manuallySuppressedIssues = mutableSetOf<String>()

    internal fun createBaseline() = DefaultBaseline(manuallySuppressedIssues, currentIssues)

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            MANUALLY_SUPPRESSED_ISSUES -> current = MANUALLY_SUPPRESSED_ISSUES
            CURRENT_ISSUES -> current = CURRENT_ISSUES
            ID -> content = ""
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if (qName == ID) {
            check(content.isNotBlank()) { "The content of the ID element must not be empty" }
            when (current) {
                MANUALLY_SUPPRESSED_ISSUES -> manuallySuppressedIssues.add(content)
                CURRENT_ISSUES -> currentIssues.add(content)
            }
            content = ""
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (current != null) content += String(ch, start, length)
    }
}
