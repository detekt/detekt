package io.gitlab.arturbosch.detekt.report

import com.android.utils.forEach
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * A naive implementation to merge xml assuming all input xml are written by the standard xml format.
 */
internal object XmlOutputMerger {

    private val documentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    fun merge(inputs: Collection<File>, output: File) {
        val document = documentBuilder.newDocument()
        document.appendChild(document.createElement("checkstyle"))
        document.xmlStandalone = true
        inputs.forEach {
            importNodesFromInput(it, document)
        }
        TransformerFactory.newInstance().newTransformer().run {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            val streamResult = StreamResult(output.writer())
            transform(DOMSource(document), streamResult)
        }
    }

    private fun importNodesFromInput(input: File, document: Document) {
        if (!input.exists()) {
            return
        }
        removeWhitespaces(documentBuilder.parse(input).documentElement).childNodes.forEach { node ->
            document.documentElement.appendChild(document.importNode(node, true))
        }
    }

    // Checkstyle does not provide XSD schema https://github.com/checkstyle/checkstyle/issues/7517.
    // Therefore we have to exclude whitespaces ourselves.
    private fun removeWhitespaces(node: Node): Node {
        val childNodes = node.childNodes
        (childNodes.length - 1 downTo 0).forEach { idx ->
            val childNode = childNodes.item(idx)
            if (childNode.nodeType == Node.TEXT_NODE && childNode.textContent.isBlank()) {
                node.removeChild(childNode)
            } else {
                removeWhitespaces(childNode)
            }
        }
        return node
    }
}
