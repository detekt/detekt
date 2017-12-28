package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.cli.baseline.internal.IndentingXMLStreamWriter
import org.xml.sax.SAXParseException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

/**
 * @author Artur Bosch
 */
object BaselineFormat {

	private val outputFactory by lazy { XMLOutputFactory.newFactory() }
	private val inputFactory by lazy { SAXParserFactory.newInstance() }
	private fun xmlReader() = inputFactory.newSAXParser()
	private fun xmlWriter(out: OutputStream) = outputFactory.createXMLStreamWriter(out, StandardCharsets.UTF_8.name())

	fun read(path: Path): Baseline {
		try {
			Files.newInputStream(path).use {
				val reader = xmlReader()
				val handler = BaselineHandler()
				reader.parse(it, handler)
				return handler.createBaseline()
			}
		} catch (error: SAXParseException) {
			val (line, column) = error.lineNumber to error.columnNumber
			throw InvalidBaselineState("Error on position $line:$column while reading the baseline xml file!", error)
		}
	}

	fun write(baseline: Baseline, path: Path) {
		try {
			Files.newOutputStream(path).use {
				val writer = IndentingXMLStreamWriter(xmlWriter(it))
				writer.save(baseline)
			}
		} catch (error: XMLStreamException) {
			val (line, column) = error.positions
			throw InvalidBaselineState("Error on position $line:$column while writing the baseline xml file!", error)
		}
	}

	private val XMLStreamException.positions
		get() = location.lineNumber to location.columnNumber

	private fun XMLStreamWriter.save(baseline: Baseline) {
		document {
			tag(SMELL_BASELINE) {
				tag(BLACKLIST) {
					val (ids, timestamp) = baseline.blacklist
					attribute(TIMESTAMP, timestamp)
					ids.forEach { tag(ID, it) }
				}
				tag(WHITELIST) {
					val (ids, timestamp) = baseline.whitelist
					attribute(TIMESTAMP, timestamp)
					ids.forEach { tag(ID, it) }
				}
			}
		}
	}
}

