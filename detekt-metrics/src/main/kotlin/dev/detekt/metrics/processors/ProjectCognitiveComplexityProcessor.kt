package dev.detekt.metrics.processors

import dev.detekt.api.DetektVisitor
import dev.detekt.metrics.CognitiveComplexity
import org.jetbrains.kotlin.psi.KtFile

class ProjectCognitiveComplexityProcessor : AbstractProcessor() {

    override val id: String = "ProjectCognitiveComplexityProcessor"

    override val visitor = object : DetektVisitor() {

        override fun visitKtFile(file: KtFile) {
            val complexity = CognitiveComplexity.calculate(file)
            file.putUserData(CognitiveComplexity.KEY, complexity)
        }
    }

    override val key = CognitiveComplexity.KEY
}
