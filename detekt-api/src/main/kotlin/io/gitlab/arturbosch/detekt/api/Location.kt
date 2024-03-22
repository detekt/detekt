package io.gitlab.arturbosch.detekt.api

import dev.drewhamilton.poko.Poko
import io.github.detekt.psi.FilePath
import io.github.detekt.psi.getLineAndColumnInPsiFile
import io.github.detekt.psi.toFilePath
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Specifies a position within a source code fragment.
 */
class Location(
    val source: SourceLocation,
    val endSource: SourceLocation = source,
    val text: TextLocation,
    val filePath: FilePath
) : Compactable {

    override fun compact(): String = "${filePath.absolutePath}:$source"

    override fun toString(): String =
        "Location(source=$source, endSource=$endSource, text=$text, filePath=$filePath)"

    companion object {
        /**
         * Creates a [Location] from a [PsiElement].
         * If the element can't be determined, the [KtFile] with a character offset can be used.
         */
        fun from(element: PsiElement, offset: Int = 0): Location {
            val start = startLineAndColumn(element, offset)
            val sourceLocation = SourceLocation(start.line, start.column)
            val end = endLineAndColumn(element, offset)
            val endSourceLocation = SourceLocation(end.line, end.column)
            val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
            return Location(sourceLocation, endSourceLocation, textLocation, element.containingFile.toFilePath())
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

        private fun lineAndColumn(element: PsiElement, range: TextRange): PsiDiagnosticUtils.LineAndColumn {
            return getLineAndColumnInPsiFile(element.containingFile, range)
                ?: PsiDiagnosticUtils.LineAndColumn(1, 1, null)
        }
    }
}

/**
 * Stores line and column information of a location.
 */
@Poko
class SourceLocation(val line: Int, val column: Int) {
    init {
        require(line > 0) { "The source location line must be greater than 0" }
        require(column > 0) { "The source location column must be greater than 0" }
    }

    override fun toString(): String = "$line:$column"
}

/**
 * Stores character start and end positions of a text file.
 */
@Poko
class TextLocation(val start: Int, val end: Int) {
    override fun toString(): String = "$start:$end"
}
