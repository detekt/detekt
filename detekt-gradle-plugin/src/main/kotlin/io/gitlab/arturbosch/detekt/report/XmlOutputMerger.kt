package io.gitlab.arturbosch.detekt.report

import java.io.File

/**
 * A naive implementation to merge xml assuming all input xml are written by the standard xml format.
 */
internal object XmlOutputMerger {

    private const val XML_PROLOG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
    private const val CHECKSTYLE_OPEN_TAG = "<checkstyle version=\"4.3\">"
    private const val CHECKSTYLE_CLOSE_TAG = "</checkstyle>"
    private const val MIN_LINES = 3

    fun merge(inputs: Collection<File>, output: File) {
        output.printWriter().use { printWriter ->
            printWriter.println(XML_PROLOG)
            printWriter.println(CHECKSTYLE_OPEN_TAG)
            inputs.forEach {
                extractInputContent(it)?.let(printWriter::println)
            }
            printWriter.println(CHECKSTYLE_CLOSE_TAG)
        }
    }

    private fun extractInputContent(input: File): String? {
        if (!input.exists()) {
            return null
        }
        val lines = input.readLines()
        if (lines.size < MIN_LINES) return null
        return if (lines.first().trim() == XML_PROLOG &&
            lines[1].trim() == CHECKSTYLE_OPEN_TAG &&
            lines.last().trim() == CHECKSTYLE_CLOSE_TAG) {
            lines.subList(2, lines.size - 1).joinToString(separator = System.lineSeparator())
        } else {
            null
        }
    }
}
