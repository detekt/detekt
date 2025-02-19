package io.gitlab.arturbosch.detekt.core.baseline

import javax.xml.stream.XMLStreamWriter

@Suppress("TooManyFunctions")
internal class IndentingXMLStreamWriter(
    private val writer: XMLStreamWriter,
    private val indent: String = "  ",
) : XMLStreamWriter by writer {

    private var currentState = NOTHING
    private val stateStack = ArrayDeque<Any>()

    private var indentationDepth = 0

    private fun onStartTag() {
        stateStack.addFirst(TAG)
        currentState = NOTHING
        writeNL()
        writeIndent()
        indentationDepth++
    }

    private fun onEndTag() {
        indentationDepth--
        if (currentState === TAG) {
            writer.writeCharacters("\n")
            writeIndent()
        }
        currentState = stateStack.removeFirst()
    }

    private fun onEmptyTag() {
        currentState = TAG
        writeNL()
        writeIndent()
    }

    private fun writeNL() {
        if (indentationDepth > 0) {
            writer.writeCharacters("\n")
        }
    }

    private fun writeIndent() {
        if (indentationDepth > 0) {
            repeat(indentationDepth) {
                writer.writeCharacters(indent)
            }
        }
    }

    override fun writeStartDocument() {
        writer.writeStartDocument()
        writer.writeCharacters("\n")
    }

    override fun writeStartDocument(version: String) {
        writer.writeStartDocument(version)
        writer.writeCharacters("\n")
    }

    override fun writeStartDocument(encoding: String, version: String) {
        writer.writeStartDocument(encoding, version)
        writer.writeCharacters("\n")
    }

    override fun writeStartElement(localName: String) {
        onStartTag()
        writer.writeStartElement(localName)
    }

    override fun writeStartElement(namespaceURI: String, localName: String) {
        onStartTag()
        writer.writeStartElement(namespaceURI, localName)
    }

    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        onStartTag()
        writer.writeStartElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        onEmptyTag()
        writer.writeEmptyElement(namespaceURI, localName)
    }

    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        onEmptyTag()
        writer.writeEmptyElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(localName: String) {
        onEmptyTag()
        writer.writeEmptyElement(localName)
    }

    override fun writeEndElement() {
        onEndTag()
        writer.writeEndElement()
    }

    override fun writeCharacters(text: String) {
        currentState = DATA
        writer.writeCharacters(text)
    }

    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        currentState = DATA
        writer.writeCharacters(text, start, len)
    }

    override fun writeCData(data: String) {
        currentState = DATA
        writer.writeCData(data)
    }

    companion object {
        private val NOTHING = Any()
        private val TAG = Any()
        private val DATA = Any()
    }
}
