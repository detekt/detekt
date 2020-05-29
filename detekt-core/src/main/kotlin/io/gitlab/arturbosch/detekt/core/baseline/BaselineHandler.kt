package io.gitlab.arturbosch.detekt.core.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

internal class BaselineHandler : DefaultHandler() {

    private var current: String? = null
    private var content: String = ""
    private val whiteIds = mutableSetOf<String>()
    private val blackIds = mutableSetOf<String>()

    internal fun createBaseline() = Baseline(blackIds, whiteIds)

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            BLACKLIST -> {
                current = BLACKLIST
            }
            WHITELIST -> {
                current = WHITELIST
            }
            ID -> content = ""
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            ID -> {
                check(content.isNotBlank()) { "The content of the ID element must not be empty" }
                when (current) {
                    BLACKLIST -> blackIds.add(content)
                    WHITELIST -> whiteIds.add(content)
                }
                content = ""
            }
            BLACKLIST, WHITELIST -> current == null
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (current != null) content += String(ch, start, length)
    }
}
