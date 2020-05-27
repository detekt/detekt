package io.gitlab.arturbosch.detekt.core.baseline

import javax.xml.stream.XMLStreamWriter

internal abstract class DelegatingXMLStreamWriter(writer: XMLStreamWriter) : XMLStreamWriter by writer
