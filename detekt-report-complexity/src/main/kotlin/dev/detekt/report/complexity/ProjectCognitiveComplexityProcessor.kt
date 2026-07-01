package dev.detekt.report.complexity

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import dev.detekt.metrics.CognitiveComplexity
import org.jetbrains.kotlin.psi.KtFile

class ProjectCognitiveComplexityProcessor : AbstractProcessor() {

    override val id: String = "ProjectCognitiveComplexityProcessor"

    override val visitor = object : DetektVisitor() {

        override fun visitKtFile(file: KtFile) {
            val complexity = CognitiveComplexity.calculate(file)
            file.putUserData(cognitiveComplexityKey, complexity)
        }
    }

    override val key = cognitiveComplexityKey
}

val cognitiveComplexityKey = Key<Int>("detekt.metrics.cognitive_complexity")
