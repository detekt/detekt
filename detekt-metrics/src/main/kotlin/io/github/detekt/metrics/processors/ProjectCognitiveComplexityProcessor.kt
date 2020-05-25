package io.github.detekt.metrics.processors

import io.github.detekt.metrics.CognitiveComplexity
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile

class ProjectCognitiveComplexityProcessor : AbstractProcessor() {

    override val visitor = object : DetektVisitor() {

        override fun visitKtFile(file: KtFile) {
            val complexity = CognitiveComplexity.calculate(file)
            file.putUserData(CognitiveComplexity.KEY, complexity)
        }
    }

    override val key = CognitiveComplexity.KEY
}
