package dev.detekt.generator.util

import dev.detekt.generator.collection.Collector
import dev.detekt.test.compileContentForTest
import org.intellij.lang.annotations.Language

fun <T> Collector<T>.run(@Language("kotlin") code: String): List<T> {
    val ktFile = compileContentForTest(code.trimIndent())
    visit(ktFile)
    return items
}
