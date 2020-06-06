package io.gitlab.arturbosch.detekt.core.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

internal class BaselineHandler : DefaultHandler() {

    private var current: String? = null
    private var content: String = ""
    private val temporarySuppressedIds = mutableSetOf<String>()
    private val falsePositiveIds = mutableSetOf<String>()

    internal fun createBaseline() = Baseline(falsePositiveIds, temporarySuppressedIds)

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            SUPPRESSED_FALSE_POSITIVES -> {
                current = SUPPRESSED_FALSE_POSITIVES
            }
            TEMPORARY_SUPPRESSED_ISSUES -> {
                current = TEMPORARY_SUPPRESSED_ISSUES
            }
            ID -> content = ""
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            ID -> {
                check(content.isNotBlank()) { "The content of the ID element must not be empty" }
                when (current) {
                    SUPPRESSED_FALSE_POSITIVES -> falsePositiveIds.add(content)
                    TEMPORARY_SUPPRESSED_ISSUES -> temporarySuppressedIds.add(content)
                }
                content = ""
            }
            SUPPRESSED_FALSE_POSITIVES, TEMPORARY_SUPPRESSED_ISSUES -> current == null
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (current != null) content += String(ch, start, length)
    }
}
