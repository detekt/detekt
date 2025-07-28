package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile

class ProjectSLOCProcessor : AbstractProcessor() {

    override val id: String = "ProjectSLOCProcessor"
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

        fun count(lines: List<String>): Int =
            lines
                .map { it.trim() }
                .count { trim -> trim.isNotEmpty() && !comments.any { trim.startsWith(it) } }
    }
}

val sourceLinesKey = Key<Int>("sloc")
