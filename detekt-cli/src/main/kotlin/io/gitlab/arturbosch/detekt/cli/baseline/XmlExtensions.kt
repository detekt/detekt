package io.gitlab.arturbosch.detekt.cli.baseline

import java.io.Writer
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

fun Writer.streamXml(): XMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(this)

fun XMLStreamWriter.prettyPrinter(): XMLStreamWriter = IndentingXMLStreamWriter(this)

inline fun XMLStreamWriter.document(
    version: String? = null,
    encoding: String? = null,
    init: XMLStreamWriter.() -> Unit
) = apply {
    when {
        encoding != null && version != null -> writeStartDocument(encoding, version)
        version != null -> writeStartDocument(version)
        else -> writeStartDocument()
    }
    init()
    writeEndDocument()
}

inline fun XMLStreamWriter.tag(
    name: String,
    init: XMLStreamWriter.() -> Unit
) = apply {
    writeStartElement(name)
    init()
    writeEndElement()
}

fun XMLStreamWriter.tag(name: String, content: String) {
    tag(name) {
        writeCharacters(content)
    }
}
