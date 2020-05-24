package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectLOCProcessor : AbstractProcessor() {

    override val visitor: DetektVisitor = LOCVisitor()
    override val key = linesKey
}

class LOCVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val lines = file.text.count { it == '\n' } + 1
        file.putUserData(linesKey, lines)
    }
}

val linesKey = Key<Int>("loc")
