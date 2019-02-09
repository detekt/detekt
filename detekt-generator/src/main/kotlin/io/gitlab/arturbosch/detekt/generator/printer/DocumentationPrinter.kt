package io.gitlab.arturbosch.detekt.generator.printer

/**
 * @author Marvin Ramin
 */
interface DocumentationPrinter<in T> {
    fun print(item: T): String
}
