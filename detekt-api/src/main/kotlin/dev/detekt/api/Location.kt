package dev.detekt.api

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnInPsiFile
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import kotlin.io.path.Path

/**
 * Creates a [Location] from a [PsiElement].
 * If the element can't be determined, the [KtFile] with a character offset can be used.
 */
fun Location.Companion.from(element: PsiElement, offset: Int = 0): Location {
    val start = startLineAndColumn(element, offset)
    val sourceLocation = SourceLocation(start.line, start.column)
    val end = endLineAndColumn(element, offset)
    val endSourceLocation = SourceLocation(end.line, end.column)
    val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
    return Location(
        sourceLocation,
        endSourceLocation,
        textLocation,
        Path((element.containingFile as KtFile).virtualFilePath)
    )
}

/**
 * Determines the start line and column of a [PsiElement] in the source file.
 */
private fun startLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn =
    lineAndColumn(
        element,
        TextRange(element.textRange.startOffset + offset, element.textRange.endOffset + offset)
    )

/**
 * Determines the end line and column of a [PsiElement] in the source file.
 */
private fun endLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn =
    lineAndColumn(
        element,
        TextRange(element.textRange.endOffset + offset, element.textRange.endOffset + offset)
    )

private fun lineAndColumn(element: PsiElement, range: TextRange): PsiDiagnosticUtils.LineAndColumn =
    if (element.containingFile.text.isNotEmpty()) {
        getLineAndColumnInPsiFile(element.containingFile, range)
    } else {
        PsiDiagnosticUtils.LineAndColumn(1, 1, null)
    }
