package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLoopExpression

class NumberOfLoopsProcessor : FileProcessListener {

    override fun onProcess(file: KtFile) {
        val visitor = LoopVisitor()
        file.accept(visitor)
        file.putUserData(numberOfLoopsKey, visitor.numberOfLoops)
    }

    companion object {
        val numberOfLoopsKey = Key<Int>("number of loops")
    }

    class LoopVisitor : DetektVisitor() {

        internal var numberOfLoops = 0
        override fun visitLoopExpression(loopExpression: KtLoopExpression) {
            super.visitLoopExpression(loopExpression)
            numberOfLoops++
        }
    }
}
