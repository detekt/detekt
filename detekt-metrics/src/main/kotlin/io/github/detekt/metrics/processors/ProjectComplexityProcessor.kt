package io.github.detekt.metrics.processors

import io.github.detekt.metrics.CyclomaticComplexity
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectComplexityProcessor : AbstractProcessor() {

    override val id: String = "ProjectComplexityProcessor"
    override val visitor = ComplexityVisitor()
    override val key = complexityKey
}

val complexityKey = Key<Int>("complexity")

class ComplexityVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val complexity = CyclomaticComplexity.calculate(file) {
            ignoreSimpleWhenEntries = false
        }
        file.putUserData(complexityKey, complexity)
    }
}
