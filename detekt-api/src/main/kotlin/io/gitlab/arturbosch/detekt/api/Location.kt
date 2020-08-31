package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Specifies a position within a source code fragment.
 */
data class Location(
    val source: SourceLocation,
    val text: TextLocation,
    val file: String
) : Compactable {

    @Deprecated(
        """
        locationString was removed and won't get passed to the main constructor.
        Use queries on 'ktElement' instead.
        """,
        ReplaceWith(
            "Location(source, text, file)",
            "io.gitlab.arturbosch.detekt.api.Location"
        )
    )
    constructor(
        source: SourceLocation,
        text: TextLocation,
        @Suppress("UNUSED_PARAMETER") locationString: String,
        file: String
    ) : this(source, text, file)

    override fun compact(): String = "$file:$source"

    companion object {
        /**
         * Creates a [Location] from a [PsiElement].
         * If the element can't be determined, the [KtFile] with a character offset can be used.
         */
        fun from(element: PsiElement, offset: Int = 0): Location {
            val start = startLineAndColumn(element, offset)
            val sourceLocation = SourceLocation(start.line, start.column)
            val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
            val fileName = element.containingFile.name
            return Location(sourceLocation, textLocation, fileName)
        }

        /**
         * Determines the line and column of a [PsiElement] in the source file.
         */
        @Suppress("TooGenericExceptionCaught")
        fun startLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn {
            return try {
                val range = element.textRange
                DiagnosticUtils.getLineAndColumnInPsiFile(element.containingFile,
                    TextRange(range.startOffset + offset, range.endOffset + offset))
            } catch (e: IndexOutOfBoundsException) {
                // #18 - somehow the TextRange is out of bound on '}' leaf nodes, returning fail safe -1
                PsiDiagnosticUtils.LineAndColumn(-1, -1, null)
            }
        }
    }
}

/**
 * Stores line and column information of a location.
 */
data class SourceLocation(val line: Int, val column: Int) {
    override fun toString(): String = "$line:$column"
}

/**
 * Stores character start and end positions of an text file.
 */
data class TextLocation(val start: Int, val end: Int) {
    override fun toString(): String = "$start:$end"
}
