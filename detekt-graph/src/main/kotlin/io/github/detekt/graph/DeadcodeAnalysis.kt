package io.github.detekt.graph

import io.github.detekt.graph.api.Attribute
import io.github.detekt.graph.api.Graph
import io.github.detekt.graph.api.Node
import io.github.detekt.graph.api.putValue
import java.util.ArrayDeque

fun computeReachability(graph: Graph, entries: Set<String>) {
    val reached = HashSet<Node>()
    val stack = ArrayDeque<Node>()
    stack.addAll(entries.map { graph.entryNodeByName(it) })
    while (stack.isNotEmpty()) {
        val current = stack.pop()

        if (current in reached) {
            continue
        }

        graph.outgoingEdges(current).forEach { stack.add(it.target) }
        current.putValue(Attribute.IS_REACHABLE, true)
        reached.add(current)
    }
}

fun Graph.entryNodeByName(name: String): Node {
    val node = nodeByFqName(name) ?: error("Expected entry node '$name' to exist in graph.")
    node.putValue(Attribute.IS_ENTRY, true)
    return node
}
