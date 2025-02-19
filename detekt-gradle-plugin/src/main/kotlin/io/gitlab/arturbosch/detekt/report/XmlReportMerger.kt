package io.gitlab.arturbosch.detekt.report

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * A naive implementation to merge xml assuming all input xml are written by detekt.
 */
internal object XmlReportMerger {

    private val documentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    fun merge(reportFiles: Collection<File>, output: File) {
        val distinctErrorsBySourceFile = DetektCheckstyleReports(reportFiles)
            .parseCheckstyleToSourceFileNodes()
            .distinctErrorsGroupedBySourceFile()

        val mergedCheckstyle = createMergedCheckstyle(distinctErrorsBySourceFile)

        TransformerFactory.newInstance().newTransformer().run {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transform(DOMSource(mergedCheckstyle), StreamResult(output.writer()))
        }
    }

    private fun createMergedCheckstyle(distinctErrorsBySourceFile: Map<String, List<Node>>): Document {
        val mergedDocument = documentBuilder.newDocument().apply {
            xmlStandalone = true
        }
        val mergedCheckstyleNode = mergedDocument.createElement("checkstyle")
        mergedCheckstyleNode.setAttribute("version", "4.3")
        mergedDocument.appendChild(mergedCheckstyleNode)

        distinctErrorsBySourceFile.forEach { (fileName, errorNodes) ->
            mergedCheckstyleNode.appendChild(
                mergedDocument.createElement("file").apply {
                    setAttribute("name", fileName)
                    errorNodes.forEach {
                        appendChild(mergedDocument.importNode(it, true))
                    }
                }
            )
        }
        return mergedDocument
    }

    /** A list of checkstyle xml files written by detekt. */
    private class DetektCheckstyleReports(private val files: Collection<File>) {

        /**
         * Parses a list of `file` nodes matching the following topology
         *
         * ```xml
         * <checkstyle>
         *   <file/>
         * </checkstyle>
         * ```
         *
         * @see CheckstyleSourceFileNodes
         */
        fun parseCheckstyleToSourceFileNodes() =
            CheckstyleSourceFileNodes(
                files.filter { reportFile -> reportFile.exists() }
                    .flatMap { existingReportFile ->
                        val checkstyleNode = documentBuilder.parse(existingReportFile)
                        checkstyleNode.documentElement.childNodes.asSequence().filterWhitespace()
                    }
            )
    }

    /**
     * A list of checkstyle `file` nodes that may contain 0 to many `error` nodes
     *
     * ```xml
     * <file>
     *     <error>
     * </file>
     * ```
     */
    private class CheckstyleSourceFileNodes(private val nodes: List<Node>) {

        /** Returns a map containing only distinct error nodes, grouped by file name */
        fun distinctErrorsGroupedBySourceFile() = nodes
            .flatMap { fileNode ->
                val fileNameAttribute = fileNode.attributes.getNamedItem("name").nodeValue
                val errorNodes = fileNode.childNodes.asSequence().filterWhitespace()
                errorNodes.map { errorNode ->
                    CheckstyleErrorNodeWithFileData(
                        errorID = errorID(fileNameAttribute, errorNode),
                        fileName = fileNameAttribute,
                        errorNode = errorNode
                    )
                }
            }
            .distinctBy { it.errorID }
            .groupBy({ it.fileName }, { it.errorNode })

        private fun errorID(fileNameAttribute: String, errorNode: Node): Any {
            // error nodes are expected to take the form of at least <error line="#" column="#" source="ruleName"/>
            val line = errorNode.attributes.getNamedItem("line")?.nodeValue
            val column = errorNode.attributes.getNamedItem("column")?.nodeValue
            val source = errorNode.attributes.getNamedItem("source")?.nodeValue

            return if (line != null && column != null && source != null) {
                // data class provides convenient hashCode/equals based on these attributes
                ErrorID(fileName = fileNameAttribute, line = line, column = column, source = source)
            } else {
                // if the error node does not contain the expected attributes,
                // use org.w3c.dom.Node's more strict hashCode/equals method to determine error uniqueness
                errorNode
            }
        }

        private class CheckstyleErrorNodeWithFileData(
            val errorID: Any,
            val fileName: String,
            val errorNode: Node,
        )

        private data class ErrorID(
            val fileName: String,
            val line: String,
            val column: String,
            val source: String,
        )
    }

    /**
     * Use code instead of XSLT to exclude whitespaces.
     */
    private fun Sequence<Node>.filterWhitespace(): Sequence<Node> = filterNot {
        it.nodeType == Node.TEXT_NODE && it.textContent.isBlank()
    }

    private fun NodeList.asSequence() = sequence {
        for (index in 0 until length) {
            yield(item(index))
        }
    }
}
