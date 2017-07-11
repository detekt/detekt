package io.gitlab.arturbosch.detekt.cli.baseline

import javax.xml.stream.XMLStreamWriter

fun XMLStreamWriter.document(init: XMLStreamWriter.() -> Unit) = apply {
	writeStartDocument()
	init()
	writeEndDocument()
}

fun XMLStreamWriter.tag(name: String, init: XMLStreamWriter.() -> Unit) = apply {
	writeStartElement(name)
	init()
	writeEndElement()
}

fun XMLStreamWriter.tag(name: String, content: String) {
	tag(name) {
		writeCharacters(content)
	}
}

fun XMLStreamWriter.attribute(name: String, value: String) = writeAttribute(name, value)
