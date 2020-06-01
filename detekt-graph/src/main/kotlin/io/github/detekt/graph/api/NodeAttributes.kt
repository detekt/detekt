package io.github.detekt.graph.api

fun Node.isEntry(): Boolean = getValue<Boolean>(Attribute.IS_ENTRY) == true

fun Node.isReachable(): Boolean = getValue<Boolean>(Attribute.IS_REACHABLE) == true
