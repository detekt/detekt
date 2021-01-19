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
 * A naive implementation to merge xml assuming all input xml are written by detekt.
 */
object XmlReportMerger {

    private val documentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    fun merge(inputs: Collection<File>, output: File) {
        val document = documentBuilder.newDocument().apply {
            xmlStandalone = true
            val checkstyleNode = createElement("checkstyle")
            checkstyleNode.setAttribute("version", "4.3")
            appendChild(checkstyleNode)
        }
        inputs.filter { it.exists() }.forEach {
            importNodesFromInput(it, document)
        }
        TransformerFactory.newInstance().newTransformer().run {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transform(DOMSource(document), StreamResult(output.writer()))
        }
    }

    private fun importNodesFromInput(input: File, document: Document) {
        val checkstyleNode = documentBuilder.parse(input.inputStream()).documentElement.also { removeWhitespaces(it) }
        checkstyleNode.childNodes.forEach { node ->
            document.documentElement.appendChild(document.importNode(node, true))
        }
    }

    /**
     * Use code instead of XSLT to exclude whitespaces.
     */
    private fun removeWhitespaces(node: Node) {
        (node.childNodes.length - 1 downTo 0).forEach { idx ->
            val childNode = node.childNodes.item(idx)
            if (childNode.nodeType == Node.TEXT_NODE && childNode.textContent.isBlank()) {
                node.removeChild(childNode)
            } else {
                removeWhitespaces(childNode)
            }
        }
    }
}
