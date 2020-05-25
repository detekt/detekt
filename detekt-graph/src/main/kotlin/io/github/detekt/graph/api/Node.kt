package io.github.detekt.graph.api

interface Node {

    enum class Type {
        FILE, CLASS, INTERFACE, FUNCTION, PACKAGE
    }

    val type: Type
    val name: String
}
