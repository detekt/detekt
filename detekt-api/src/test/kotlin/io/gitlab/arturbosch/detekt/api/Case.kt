package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.resourceAsPath
import java.nio.file.Path

enum class Case(val file: String) {
    FilteredClass("FilteredClass.kt"),
    SuppressedObject("SuppressedObject.kt"),
    SuppressedByAllObject("SuppressedByAllObject.kt"),
    SuppressedElements("SuppressedElements.kt");

    fun path(): Path = resourceAsPath(file)
}
