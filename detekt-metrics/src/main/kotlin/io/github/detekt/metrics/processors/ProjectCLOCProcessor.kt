package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile

class ProjectCLOCProcessor : AbstractProcessor() {

    override val key = commentLinesKey
    override val visitor = CLOCVisitor()
}

val commentLinesKey = Key<Int>("cloc")

class CLOCVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        with(CLOCCountVisitor()) {
            file.accept(this)
            file.putUserData(commentLinesKey, count)
        }
    }
}

internal class CLOCCountVisitor : DetektVisitor() {

    internal var count = 0

    private fun increment(value: Int) {
        count += value
    }

    override fun visitComment(comment: PsiComment?) {
        if (comment != null) {
            increment(comment.text.split('\n').size)
        }
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        val text = dcl.docComment?.text
        if (text != null) {
            increment(text.split('\n').size)
        }
        super.visitDeclaration(dcl)
    }
}
