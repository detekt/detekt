package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

internal class BaselineHandler : DefaultHandler() {

    private var current: String? = null
    private var content: String = ""
    private val currentIssues = mutableSetOf<String>()
    private val manuallySuppressedIssues = mutableSetOf<String>()

    internal fun createBaseline() = Baseline(manuallySuppressedIssues, currentIssues)

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (qName) {
            // Blacklist and Whitelist were previous XML tags. They have been replaced by more appropriate names
            // For the removal of these to not be a breaking change these have been implemented to be synonyms
            // of [MANUALLY_SUPPRESSED_ISSUES] and [CURRENT_ISSUES].
            MANUALLY_SUPPRESSED_ISSUES, BLACKLIST -> {
                current = MANUALLY_SUPPRESSED_ISSUES
            }
            CURRENT_ISSUES, WHITELIST -> {
                current = CURRENT_ISSUES
            }
            ID -> content = ""
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        when (qName) {
            ID -> {
                check(content.isNotBlank()) { "The content of the ID element must not be empty" }
                when (current) {
                    MANUALLY_SUPPRESSED_ISSUES -> manuallySuppressedIssues.add(content)
                    CURRENT_ISSUES -> currentIssues.add(content)
                }
                content = ""
            }
            MANUALLY_SUPPRESSED_ISSUES, CURRENT_ISSUES -> current == null
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (current != null) content += String(ch, start, length)
    }

    companion object {
        @NotApiButProbablyUsedByUsers
        private const val BLACKLIST = "Blacklist"

        @NotApiButProbablyUsedByUsers
        private const val WHITELIST = "Whitelist"
    }
}
