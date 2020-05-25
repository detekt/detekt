package io.github.detekt.graph.api

interface Edge {

    val source: Node
    val target: Node
    val type: Type

    enum class Type {
        ENCLOSES,
        DECLARES
    }
}
