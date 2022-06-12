package io.gitlab.arturbosch.detekt.core.baseline

import org.xml.sax.SAXParseException
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

internal class BaselineFormat {

    private val XMLStreamException.positions
        get() = location.lineNumber to location.columnNumber

    class InvalidState(msg: String, error: Throwable) : IllegalStateException(msg, error)

    fun read(path: Path): Baseline {
        try {
            Files.newInputStream(path).use {
                val reader = SAXParserFactory.newInstance()
                    .apply {
                        setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                    }
                    .newSAXParser()
                val handler = BaselineHandler()
                reader.parse(it, handler)
                return handler.createBaseline()
            }
        } catch (error: SAXParseException) {
            val (line, column) = error.lineNumber to error.columnNumber
            throw InvalidState("Error on position $line:$column while reading the baseline xml file!", error)
        }
    }

    fun write(baseline: Baseline, path: Path) {
        try {
            Files.newBufferedWriter(path).addFinalNewLine().use {
                it.streamXml().prettyPrinter().save(baseline)
            }
        } catch (error: XMLStreamException) {
            val (line, column) = error.positions
            throw InvalidState("Error on position $line:$column while writing the baseline xml file!", error)
        }
    }

    private fun XMLStreamWriter.save(baseline: Baseline) {
        document {
            tag(SMELL_BASELINE) {
                tag(MANUALLY_SUPPRESSED_ISSUES) {
                    baseline.manuallySuppressedIssues.forEach { tag(ID, it) }
                }
                tag(CURRENT_ISSUES) {
                    baseline.currentIssues.forEach { tag(ID, it) }
                }
            }
        }
    }
}
