package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

enum class Case(val file: String) {
    FilteredClass("FilteredClass.kt"),
    SuppressedObject("SuppressedObject.kt"),
    SuppressedByAllObject("SuppressedByAllObject.kt"),
    SuppressedElements("SuppressedElements.kt");

    fun path(): Path = Paths.get(resource(file))
}
