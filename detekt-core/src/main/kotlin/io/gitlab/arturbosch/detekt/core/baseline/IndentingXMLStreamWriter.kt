package io.gitlab.arturbosch.detekt.core.baseline

import java.util.Stack
import javax.xml.stream.XMLStreamWriter

@Suppress("TooManyFunctions")
internal class IndentingXMLStreamWriter(
    writer: XMLStreamWriter,
    private val indent: String = "  "
) : DelegatingXMLStreamWriter(writer) {

    private var currentState = NOTHING
    private val stateStack = Stack<Any>()

    private var indentationDepth = 0

    private fun onStartTag() {
        stateStack.push(TAG)
        currentState = NOTHING
        writeNL()
        writeIndent()
        indentationDepth++
    }

    private fun onEndTag() {
        indentationDepth--
        if (currentState === TAG) {
            super.writeCharacters("\n")
            writeIndent()
        }
        currentState = stateStack.pop()
    }

    private fun onEmptyTag() {
        currentState = TAG
        writeNL()
        writeIndent()
    }

    private fun writeNL() {
        if (indentationDepth > 0) {
            super.writeCharacters("\n")
        }
    }

    @Suppress("RedundantLambdaArrow")
    private fun writeIndent() {
        if (indentationDepth > 0) {
            (0 until indentationDepth).forEach { _ -> super.writeCharacters(indent) }
        }
    }

    override fun writeStartDocument() {
        super.writeStartDocument()
        super.writeCharacters("\n")
    }

    override fun writeStartDocument(version: String) {
        super.writeStartDocument(version)
        super.writeCharacters("\n")
    }

    override fun writeStartDocument(encoding: String, version: String) {
        super.writeStartDocument(encoding, version)
        super.writeCharacters("\n")
    }

    override fun writeEndDocument() {
        super.writeEndDocument()
        super.writeCharacters("\n")
    }

    override fun writeStartElement(localName: String) {
        onStartTag()
        super.writeStartElement(localName)
    }

    override fun writeStartElement(namespaceURI: String, localName: String) {
        onStartTag()
        super.writeStartElement(namespaceURI, localName)
    }

    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        onStartTag()
        super.writeStartElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        onEmptyTag()
        super.writeEmptyElement(namespaceURI, localName)
    }

    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        onEmptyTag()
        super.writeEmptyElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(localName: String) {
        onEmptyTag()
        super.writeEmptyElement(localName)
    }

    override fun writeEndElement() {
        onEndTag()
        super.writeEndElement()
    }

    override fun writeCharacters(text: String) {
        currentState = DATA
        super.writeCharacters(text)
    }

    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        currentState = DATA
        super.writeCharacters(text, start, len)
    }

    override fun writeCData(data: String) {
        currentState = DATA
        super.writeCData(data)
    }

    companion object {
        private val NOTHING = Any()
        private val TAG = Any()
        private val DATA = Any()
    }
}
