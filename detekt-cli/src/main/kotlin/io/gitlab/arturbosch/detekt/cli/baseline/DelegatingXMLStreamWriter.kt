package io.gitlab.arturbosch.detekt.cli.baseline

import javax.xml.stream.XMLStreamWriter

abstract class DelegatingXMLStreamWriter(writer: XMLStreamWriter) : XMLStreamWriter by writer
