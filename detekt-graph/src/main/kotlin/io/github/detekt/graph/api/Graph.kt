package io.github.detekt.graph.api

interface Graph {

    fun nodes(predicate: (Node) -> Boolean): Sequence<Node>
    fun outgoingEdges(node: Node): Set<Edge>
    fun incomingEdges(node: Node): Set<Edge>
}
