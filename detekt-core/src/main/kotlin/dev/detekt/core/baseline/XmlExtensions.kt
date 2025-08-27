package dev.detekt.core.baseline

import java.io.BufferedWriter
import java.io.Writer
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

internal fun Writer.addFinalNewLine(): Writer =
    object : BufferedWriter(this) {
        override fun close() {
            write("\n")
            super.close()
        }
    }

internal fun Writer.streamXml(): XMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(this)

internal fun XMLStreamWriter.prettyPrinter(): XMLStreamWriter = IndentingXMLStreamWriter(this)

internal inline fun XMLStreamWriter.document(
    version: String? = null,
    encoding: String? = null,
    init: XMLStreamWriter.() -> Unit,
) = apply {
    when {
        encoding != null && version != null -> writeStartDocument(encoding, version)
        version != null -> writeStartDocument(version)
        else -> writeStartDocument()
    }
    init()
    writeEndDocument()
}

internal inline fun XMLStreamWriter.tag(
    name: String,
    init: XMLStreamWriter.() -> Unit,
) = apply {
    writeStartElement(name)
    init()
    writeEndElement()
}

internal fun XMLStreamWriter.tag(name: String, content: String) {
    tag(name) {
        writeCharacters(content)
    }
}
