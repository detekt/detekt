package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectSLOCProcessor : AbstractProcessor() {

    override val visitor: DetektVisitor = SLOCVisitor()
    override val key: Key<Int> = sourceLinesKey
}

class SLOCVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val lines = file.text.split('\n')
        val sloc = SLOC().count(lines)
        file.putUserData(sourceLinesKey, sloc)
    }

    private class SLOC {

        private val comments = arrayOf("//", "/*", "*/", "*")

        fun count(lines: List<String>): Int {
            return lines
                    .map { it.trim() }
                    .filter { trim -> trim.isNotEmpty() && !comments.any { trim.startsWith(it) } }
                    .size
        }
    }
}

val sourceLinesKey = Key<Int>("sloc")
