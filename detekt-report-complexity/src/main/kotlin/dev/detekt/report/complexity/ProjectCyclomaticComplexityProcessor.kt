package dev.detekt.report.complexity

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import dev.detekt.metrics.CyclomaticComplexity
import org.jetbrains.kotlin.psi.KtFile

class ProjectCyclomaticComplexityProcessor : AbstractProcessor() {

    override val id: String = "ProjectCyclomaticComplexityProcessor"
    override val visitor = CyclomaticComplexityVisitor()
    override val key = cyclomaticComplexityKey
}

val cyclomaticComplexityKey = Key<Int>("cyclomaticComplexity")

class CyclomaticComplexityVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val complexity = CyclomaticComplexity.calculate(file) {
            ignoreSimpleWhenEntries = false
        }
        file.putUserData(cyclomaticComplexityKey, complexity)
    }
}
