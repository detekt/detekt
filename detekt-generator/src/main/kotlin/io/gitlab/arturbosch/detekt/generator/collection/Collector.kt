package io.gitlab.arturbosch.detekt.generator.collection

import org.jetbrains.kotlin.psi.KtFile

interface Collector<out T> {
    val items: List<T>

    fun visit(file: KtFile)
}
