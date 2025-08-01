package io.github.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import io.github.detekt.metrics.processors.util.LLOC
import org.jetbrains.kotlin.psi.KtFile

class ProjectLLOCProcessor : AbstractProcessor() {

    override val id: String = "ProjectLLOCProcessor"
    override val visitor = LLOCVisitor()
    override val key = logicalLinesKey
}

val logicalLinesKey = Key<Int>("lloc")

class LLOCVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val lines = file.text.split("\n")
        val value = LLOC.analyze(lines)
        file.putUserData(logicalLinesKey, value)
    }
}
