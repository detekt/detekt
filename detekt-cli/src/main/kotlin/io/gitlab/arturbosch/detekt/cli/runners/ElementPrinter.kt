package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtContainerNode
import org.jetbrains.kotlin.psi.KtDeclarationContainer
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStatementExpression

class ElementPrinter : DetektVisitor() {

    private val sb = StringBuilder()

    private val indentation
        get() = (0..indent).joinToString("") { "  " }

    private val KtElement.line
        get() = PsiDiagnosticUtils.offsetToLineAndColumn(
            containingFile.viewProvider.document,
            textRange.startOffset
        ).line

    private val KtElement.dump
        get() = indentation + line + ": " + javaClass.simpleName

    private var indent: Int = 0
    private var lastLine = 0

    override fun visitKtElement(element: KtElement) {
        val currentLine = element.line
        if (element.isContainer()) {
            indent++
            sb.appendLine(element.dump)
        } else {
            if (lastLine == currentLine) {
                indent++
                sb.appendLine(element.dump)
                indent--
            } else {
                sb.appendLine(element.dump)
            }
        }
        lastLine = currentLine
        super.visitKtElement(element)
        if (element.isContainer()) {
            indent--
        }
    }

    private fun KtElement.isContainer() =
        this is KtStatementExpression ||
            this is KtDeclarationContainer ||
            this is KtContainerNode

    companion object {
        fun dump(file: KtFile): String = ElementPrinter().run {
            sb.appendLine("0: " + file.javaClass.simpleName)
            visitKtFile(file)
            sb.toString()
        }
    }
}
