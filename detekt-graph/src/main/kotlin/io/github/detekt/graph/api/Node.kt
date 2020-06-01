package io.github.detekt.graph.api

interface Node {

    enum class Type {
        FILE, CLASS, INTERFACE, FUNCTION, PACKAGE, CONSTRUCTOR
    }

    val type: Type
    val name: String

    val data: MutableMap<Attribute, Any>
}

inline fun <reified T : Any> Node.getValue(attribute: Attribute): T? {
    val value = data[attribute]
    if (value != null && value !is T) {
        error("Value '$value' of node '$name' has unexpected type '${value::class}'")
    }
    return value as? T
}

inline fun <reified T : Any> Node.putValue(attribute: Attribute, value: T) {
    data[attribute] = value
}
