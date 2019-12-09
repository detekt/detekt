package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.internal.CyclomaticComplexity
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectComplexityProcessor : AbstractProcessor() {

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
