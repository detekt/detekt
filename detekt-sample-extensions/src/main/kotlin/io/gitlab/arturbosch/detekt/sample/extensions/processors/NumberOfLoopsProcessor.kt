package io.gitlab.arturbosch.detekt.sample.extensions.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import dev.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLoopExpression

class NumberOfLoopsProcessor : FileProcessListener {

    override val id: String = "NumberOfLoopsProcessor"
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
