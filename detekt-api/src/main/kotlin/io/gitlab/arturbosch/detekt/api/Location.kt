package io.gitlab.arturbosch.detekt.api

import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnInPsiFile
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Specifies a position within a source code fragment.
 */
class Location(
    val source: SourceLocation,
    val endSource: SourceLocation,
    val text: TextLocation,
    val path: Path,
) {
    override fun toString(): String =
        "Location(source=$source, endSource=$endSource, text=$text, path=$path)"

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
    }
}

/**
 * Stores line and column information of a location.
 */
@Poko
class SourceLocation(val line: Int, val column: Int) : Comparable<SourceLocation> {
    init {
        require(line > 0) { "The source location line must be greater than 0" }
        require(column > 0) { "The source location column must be greater than 0" }
    }

    override fun toString(): String = "$line:$column"

    override fun compareTo(other: SourceLocation): Int = compareValuesBy(this, other, { it.line }, { it.column })
}

/**
 * Stores character start and end positions of a text file.
 */
@Poko
class TextLocation(val start: Int, val end: Int) {
    override fun toString(): String = "$start:$end"
}
