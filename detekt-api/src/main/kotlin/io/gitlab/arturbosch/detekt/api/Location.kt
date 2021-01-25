package io.gitlab.arturbosch.detekt.api

import io.github.detekt.psi.FilePath
import io.github.detekt.psi.toFilePath
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Paths

/**
 * Specifies a position within a source code fragment.
 */
data class Location @Deprecated("Consider relative path by passing a [FilePath]") @JvmOverloads constructor(
    val source: SourceLocation,
    val text: TextLocation,
    val file: String,
    val filePath: FilePath = FilePath.fromAbsolute(Paths.get(file))
) : Compactable {

    constructor(
        source: SourceLocation,
        text: TextLocation,
        filePath: FilePath
    ) : this(source, text, filePath.absolutePath.toString(), filePath)

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

    override fun compact(): String = "${filePath.absolutePath}:$source"

    companion object {
        /**
         * Creates a [Location] from a [PsiElement].
         * If the element can't be determined, the [KtFile] with a character offset can be used.
         */
        fun from(element: PsiElement, offset: Int = 0): Location {
            val start = startLineAndColumn(element, offset)
            val sourceLocation = SourceLocation(start.line, start.column)
            val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
            return Location(sourceLocation, textLocation, element.containingFile.toFilePath())
        }

        /**
         * Determines the line and column of a [PsiElement] in the source file.
         */
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        fun startLineAndColumn(element: PsiElement, offset: Int = 0): PsiDiagnosticUtils.LineAndColumn {
            return try {
                val range = element.textRange
                DiagnosticUtils.getLineAndColumnInPsiFile(element.containingFile,
                    TextRange(range.startOffset + offset, range.endOffset + offset))
            } catch (e: IndexOutOfBoundsException) {
                // #3317 If any rule mutates the PsiElement, searching the original PsiElement may throw exception.
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
