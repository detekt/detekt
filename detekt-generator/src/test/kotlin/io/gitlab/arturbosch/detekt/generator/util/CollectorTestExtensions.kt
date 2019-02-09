package io.gitlab.arturbosch.detekt.generator.util

import io.gitlab.arturbosch.detekt.generator.collection.Collector
import io.gitlab.arturbosch.detekt.test.KtTestCompiler

fun <T> Collector<T>.run(code: String): List<T> {
    val ktFile = KtTestCompiler.compileFromContent(code.trimIndent())
    visit(ktFile)
    return items
}
