package io.gitlab.arturbosch.detekt.generator.printer

interface DocumentationPrinter<in T> {
    fun print(item: T): String
}
