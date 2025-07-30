package io.gitlab.arturbosch.detekt.core.baseline

import dev.detekt.tooling.api.Baseline
import dev.detekt.tooling.api.BaselineProvider
import dev.detekt.tooling.api.FindingsIdList
import org.xml.sax.SAXParseException
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter
import kotlin.io.path.bufferedWriter
import kotlin.io.path.inputStream

internal class BaselineFormat : BaselineProvider {

    private val XMLStreamException.positions
        get() = location.lineNumber to location.columnNumber

    class InvalidState(msg: String, error: Throwable) : IllegalStateException(msg, error)

    override fun of(manuallySuppressedIssues: FindingsIdList, currentIssues: FindingsIdList): DefaultBaseline =
        DefaultBaseline(manuallySuppressedIssues, currentIssues)

    override fun read(sourcePath: Path): DefaultBaseline {
        try {
            sourcePath.inputStream().use {
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

    override fun write(targetPath: Path, baseline: Baseline) {
        try {
            targetPath.bufferedWriter().addFinalNewLine().use {
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
                if (baseline.manuallySuppressedIssues.isEmpty()) {
                    writeEmptyElement(MANUALLY_SUPPRESSED_ISSUES)
                } else {
                    tag(MANUALLY_SUPPRESSED_ISSUES) {
                        baseline.manuallySuppressedIssues.forEach { tag(ID, it) }
                    }
                }

                if (baseline.currentIssues.isEmpty()) {
                    writeEmptyElement(CURRENT_ISSUES)
                } else {
                    tag(CURRENT_ISSUES) {
                        baseline.currentIssues.forEach { tag(ID, it) }
                    }
                }
            }
        }
    }
}
