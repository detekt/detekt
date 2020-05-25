package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Graph
import io.github.detekt.graph.api.Node
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph

fun generateGraph(files: List<KtFile>, context: BindingContext): Graph {
    val graph = files.fold(DefaultGraph()) { uberGraph, file ->
        Graphs.addGraph(uberGraph, transformFileToGraph(file))
        uberGraph
    }
    generateEdgesBetweenFiles(graph, files, context)
    return graph
}

fun generateEdgesBetweenFiles(graph: DefaultGraph, files: List<KtFile>, context: BindingContext) {
    val visitor = CallEdgesVisitor(graph, context)
    files.forEach { it.accept(visitor) }
}

fun transformFileToGraph(file: KtFile): DefaultGraph {
    val graph = DefaultGraph()
    val visitor = FileToGraphVisitor(graph)
    file.accept(visitor)
    return graph
}

class DefaultGraph : DefaultDirectedGraph<Node, Edge>(Edge::class.java), Graph {

    override fun nodes(predicate: (Node) -> Boolean): Sequence<Node> =
        vertexSet()
            .asSequence()
            .filter(predicate)

    override fun outgoingEdges(node: Node): Set<Edge> = outgoingEdgesOf(node)
    override fun incomingEdges(node: Node): Set<Edge> = incomingEdgesOf(node)
}

fun Graph.firstNode(condition: (Node) -> Boolean): Node? = nodes(condition).firstOrNull()

fun Graph.nodesOfType(type: Node.Type): Sequence<Node> = nodes { it.type == type }

sealed class DefaultNode(
    override val type: Node.Type
) : Node {

    override fun toString(): String = "Node{$name}"
}

class DefaultEdge(
    override val source: Node,
    override val target: Node,
    override val type: Edge.Type
) : Edge {

    override fun toString(): String = "Edge{${source.name} -> ${target.name}}"
}

class FileNode(override val name: String) : DefaultNode(Node.Type.FILE)
class PackageNode(override val name: String) : DefaultNode(Node.Type.PACKAGE)
class ClassNode(override val name: String) : DefaultNode(Node.Type.CLASS)
class FunctionNode(override val name: String) : DefaultNode(Node.Type.FUNCTION)
